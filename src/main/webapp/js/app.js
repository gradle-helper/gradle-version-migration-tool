let currentProject = null;
let allIssues = [];
let selectedIssues = new Set();

async function browseDirectory() {
    // Check if the File System Access API is supported
    if (!('showDirectoryPicker' in window)) {
        alert('Directory browsing is not supported in your browser.\n\n' +
              'This feature works in Chrome/Edge 86+.\n\n' +
              'Please paste the project path manually.');
        return;
    }
    
    try {
        // Show directory picker
        const dirHandle = await window.showDirectoryPicker({
            mode: 'read',
            startIn: 'documents'
        });
        
        // Get the full path (this is limited by browser security)
        // We can only get the directory name, not the full absolute path
        // So we'll need to construct it or ask the user
        
        // For now, we'll show a message explaining the limitation
        const dirName = dirHandle.name;
        
        // Try to get some path information
        let pathHint = '';
        try {
            // This won't give us the full path due to security restrictions
            // but we can at least show the directory name
            pathHint = dirName;
        } catch (e) {
            pathHint = dirName;
        }
        
        // Show a dialog to help the user
        const userPath = prompt(
            `Selected directory: "${dirName}"\n\n` +
            `Due to browser security restrictions, we cannot automatically get the full path.\n\n` +
            `Please enter or confirm the full absolute path to this directory:`,
            document.getElementById('projectPath').value || `/path/to/${dirName}`
        );
        
        if (userPath) {
            document.getElementById('projectPath').value = userPath;
        }
        
    } catch (err) {
        if (err.name !== 'AbortError') {
            console.error('Error selecting directory:', err);
            alert('Error selecting directory: ' + err.message);
        }
        // User cancelled the picker
    }
}

async function analyzeProject() {
    const projectPath = document.getElementById('projectPath').value.trim();
    
    if (!projectPath) {
        alert('Please enter a project path');
        return;
    }
    
    // Show loading indicator
    document.getElementById('loadingIndicator').style.display = 'block';
    document.getElementById('analyzeBtn').disabled = true;
    
    // Hide previous results
    document.getElementById('project-info-section').style.display = 'none';
    document.getElementById('issues-section').style.display = 'none';
    document.getElementById('results-section').style.display = 'none';
    
    try {
        const response = await fetch('/api/analyze', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `projectPath=${encodeURIComponent(projectPath)}`
        });
        
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.error || 'Failed to analyze project');
        }
        
        currentProject = await response.json();
        allIssues = currentProject.issues || [];
        selectedIssues.clear();
        
        displayProjectInfo();
        displayIssues();
        
    } catch (error) {
        alert('Error: ' + error.message);
        console.error('Analysis error:', error);
    } finally {
        document.getElementById('loadingIndicator').style.display = 'none';
        document.getElementById('analyzeBtn').disabled = false;
    }
}

function displayProjectInfo() {
    document.getElementById('projectName').textContent = currentProject.projectName || 'Unknown';
    document.getElementById('gradleVersion').textContent = currentProject.currentGradleVersion || 'Unknown';
    document.getElementById('isMultiModule').textContent = currentProject.multiModule ? 'Yes' : 'No';
    
    const modulesList = currentProject.modules && currentProject.modules.length > 0
        ? currentProject.modules.join(', ')
        : 'None';
    document.getElementById('modulesList').textContent = modulesList;
    
    document.getElementById('totalIssues').textContent = currentProject.totalIssues || 0;
    document.getElementById('criticalIssues').textContent = currentProject.criticalIssues || 0;
    document.getElementById('autoFixableIssues').textContent = currentProject.autoFixableIssues || 0;
    
    document.getElementById('project-info-section').style.display = 'block';
}

function displayIssues() {
    const issuesList = document.getElementById('issuesList');
    issuesList.innerHTML = '';
    
    if (!allIssues || allIssues.length === 0) {
        issuesList.innerHTML = '<p class="help-text">No issues found! Your project is ready for Gradle 9.</p>';
        document.getElementById('issues-section').style.display = 'block';
        return;
    }
    
    allIssues.forEach(issue => {
        const issueElement = createIssueElement(issue);
        issuesList.appendChild(issueElement);
    });
    
    document.getElementById('issues-section').style.display = 'block';
}

function createIssueElement(issue) {
    const div = document.createElement('div');
    div.className = 'issue-item';
    div.dataset.issueId = issue.id;
    div.dataset.severity = issue.severity;
    div.dataset.autoFixable = issue.autoFixable;
    
    const severityClass = `badge-${issue.severity.toLowerCase()}`;
    const autoFixableBadge = issue.autoFixable 
        ? '<span class="badge badge-fixable">Auto-Fixable</span>' 
        : '';
    
    const modulesText = issue.affectedModules && issue.affectedModules.length > 0
        ? issue.affectedModules.join(', ')
        : 'N/A';
    
    div.innerHTML = `
        <div class="issue-header">
            <div class="issue-title-section">
                <input type="checkbox" class="issue-checkbox" 
                       onchange="toggleIssueSelection('${issue.id}')" 
                       ${issue.autoFixable ? '' : 'disabled'}>
                <div>
                    <div class="issue-title">${escapeHtml(issue.title)}</div>
                </div>
            </div>
            <div class="issue-badges">
                <span class="badge ${severityClass}">${issue.severity}</span>
                ${autoFixableBadge}
            </div>
        </div>
        
        <div class="issue-details">
            <p class="issue-description">${escapeHtml(issue.description)}</p>
            
            <div class="issue-meta">
                <div class="issue-meta-item">
                    <strong>📄 File:</strong> ${escapeHtml(issue.filePath)}
                </div>
                <div class="issue-meta-item">
                    <strong>📍 Line:</strong> ${issue.lineNumber}
                </div>
                <div class="issue-meta-item">
                    <strong>📦 Modules:</strong> ${escapeHtml(modulesText)}
                </div>
            </div>
            
            <div class="issue-explanation">
                <strong>💡 Detailed Explanation:</strong>
                <p>${escapeHtml(issue.explanation)}</p>
            </div>
            
            <div>
                <span class="code-label">Current Code:</span>
                <div class="code-block">${escapeHtml(issue.currentCode)}</div>
            </div>
            
            ${issue.suggestedFix ? `
                <div>
                    <span class="code-label">Suggested Fix:</span>
                    <div class="code-block">${escapeHtml(issue.suggestedFix)}</div>
                </div>
            ` : ''}
        </div>
        
        ${issue.autoFixable ? `
            <div class="issue-actions">
                <button onclick="fixSingleIssue('${issue.id}')" class="btn btn-success btn-small">
                    Fix This Issue
                </button>
            </div>
        ` : ''}
    `;
    
    return div;
}

function toggleIssueSelection(issueId) {
    const checkbox = document.querySelector(`[data-issue-id="${issueId}"] .issue-checkbox`);
    const issueElement = document.querySelector(`[data-issue-id="${issueId}"]`);
    
    if (checkbox.checked) {
        selectedIssues.add(issueId);
        issueElement.classList.add('selected');
    } else {
        selectedIssues.delete(issueId);
        issueElement.classList.remove('selected');
    }
    
    updateFixButtonsState();
}

function updateFixButtonsState() {
    const fixSelectedBtn = document.getElementById('fixSelectedBtn');
    fixSelectedBtn.disabled = selectedIssues.size === 0;
}

function filterIssues() {
    const filterCritical = document.getElementById('filterCritical').checked;
    const filterHigh = document.getElementById('filterHigh').checked;
    const filterMedium = document.getElementById('filterMedium').checked;
    const filterAutoFixable = document.getElementById('filterAutoFixable').checked;
    
    const issueElements = document.querySelectorAll('.issue-item');
    
    issueElements.forEach(element => {
        const severity = element.dataset.severity;
        const autoFixable = element.dataset.autoFixable === 'true';
        
        let show = true;
        
        // Apply severity filters
        if (filterCritical || filterHigh || filterMedium) {
            show = false;
            if (filterCritical && severity === 'CRITICAL') show = true;
            if (filterHigh && severity === 'HIGH') show = true;
            if (filterMedium && severity === 'MEDIUM') show = true;
        }
        
        // Apply auto-fixable filter
        if (filterAutoFixable && !autoFixable) {
            show = false;
        }
        
        element.style.display = show ? 'block' : 'none';
    });
}

async function fixSingleIssue(issueId) {
    await fixIssues([issueId]);
}

async function fixSelected() {
    if (selectedIssues.size === 0) {
        alert('Please select at least one issue to fix');
        return;
    }
    
    await fixIssues(Array.from(selectedIssues));
}

async function fixAllAutoFixable() {
    const autoFixableIssues = allIssues
        .filter(issue => issue.autoFixable)
        .map(issue => issue.id);
    
    if (autoFixableIssues.length === 0) {
        alert('No auto-fixable issues found');
        return;
    }
    
    if (!confirm(`This will fix ${autoFixableIssues.length} issues. Continue?`)) {
        return;
    }
    
    await fixIssues(autoFixableIssues);
}

async function fixIssues(issueIds) {
    try {
        // Disable fix buttons
        document.getElementById('fixAllBtn').disabled = true;
        document.getElementById('fixSelectedBtn').disabled = true;
        
        const response = await fetch('/api/fix', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ issueIds })
        });
        
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.error || 'Failed to apply fixes');
        }
        
        const result = await response.json();
        displayFixResults(result);
        
        // Remove fixed issues from the list
        result.results.forEach(fixResult => {
            if (fixResult.success) {
                const issueElement = document.querySelector(`[data-issue-id="${fixResult.issueId}"]`);
                if (issueElement) {
                    issueElement.remove();
                }
                allIssues = allIssues.filter(issue => issue.id !== fixResult.issueId);
                selectedIssues.delete(fixResult.issueId);
            }
        });
        
        // Update stats
        if (currentProject) {
            currentProject.totalIssues = allIssues.length;
            currentProject.criticalIssues = allIssues.filter(i => i.severity === 'CRITICAL').length;
            currentProject.autoFixableIssues = allIssues.filter(i => i.autoFixable).length;
            displayProjectInfo();
        }
        
    } catch (error) {
        alert('Error: ' + error.message);
        console.error('Fix error:', error);
    } finally {
        document.getElementById('fixAllBtn').disabled = false;
        updateFixButtonsState();
    }
}

function displayFixResults(result) {
    const resultsSection = document.getElementById('results-section');
    const fixResults = document.getElementById('fixResults');
    
    let html = `
        <div class="stats-grid" style="margin-bottom: 20px;">
            <div class="stat-card stat-fixable">
                <div class="stat-number">${result.totalProcessed}</div>
                <div class="stat-label">Total Processed</div>
            </div>
            <div class="stat-card stat-total" style="background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);">
                <div class="stat-number">${result.successCount}</div>
                <div class="stat-label">Successfully Fixed</div>
            </div>
            <div class="stat-card stat-critical">
                <div class="stat-number">${result.failureCount}</div>
                <div class="stat-label">Failed</div>
            </div>
        </div>
    `;
    
    result.results.forEach(fixResult => {
        const statusClass = fixResult.success ? 'success' : 'error';
        const icon = fixResult.success ? '✅' : '❌';
        
        html += `
            <div class="fix-result-item ${statusClass}">
                <div class="fix-result-header">${icon} ${escapeHtml(fixResult.filePath)}</div>
                <div class="fix-result-message">${escapeHtml(fixResult.message)}</div>
                ${fixResult.backupPath ? `<div class="fix-result-message" style="margin-top: 5px; font-size: 0.85rem;">Backup: ${escapeHtml(fixResult.backupPath)}</div>` : ''}
            </div>
        `;
    });
    
    fixResults.innerHTML = html;
    resultsSection.style.display = 'block';
    
    // Scroll to results
    resultsSection.scrollIntoView({ behavior: 'smooth', block: 'start' });
}

function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    // Allow Enter key to trigger analysis
    document.getElementById('projectPath').addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            analyzeProject();
        }
    });
    
    updateFixButtonsState();
});
