<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gradle 9 Migration Helper</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
    <div class="container">
        <header class="header">
            <div class="header-content">
                <h1>🔧 Gradle 9 Migration Helper</h1>
                <p class="subtitle">Automatically detect and fix Gradle 9 compatibility issues</p>
            </div>
        </header>

        <main class="main-content">
            <!-- Project Selection Section -->
            <section class="card" id="project-section">
                <h2>📁 Select Project</h2>
                <p class="help-text">Enter the absolute path to your Gradle project directory</p>
                
                <div class="input-group">
                    <input 
                        type="text" 
                        id="projectPath" 
                        placeholder="/path/to/your/gradle/project"
                        class="input-field"
                    />
                    <button onclick="browseDirectory()" class="btn btn-secondary" id="browseBtn" title="Browse for directory (modern browsers only)">
                        📁 Browse
                    </button>
                    <button onclick="analyzeProject()" class="btn btn-primary" id="analyzeBtn">
                        Analyze Project
                    </button>
                </div>
                <p class="help-text" style="font-size: 0.9rem; color: #7f8c8d; margin-top: 10px;">
                    💡 Tip: You can paste the path directly or use the Browse button (Chrome/Edge only)
                </p>
                
                <div id="loadingIndicator" class="loading-indicator" style="display: none;">
                    <div class="spinner"></div>
                    <p>Analyzing project...</p>
                </div>
            </section>

            <!-- Project Info Section -->
            <section class="card" id="project-info-section" style="display: none;">
                <h2>📊 Project Information</h2>
                <div class="info-grid">
                    <div class="info-item">
                        <span class="info-label">Project Name:</span>
                        <span id="projectName" class="info-value"></span>
                    </div>
                    <div class="info-item">
                        <span class="info-label">Current Gradle Version:</span>
                        <span id="gradleVersion" class="info-value"></span>
                    </div>
                    <div class="info-item">
                        <span class="info-label">Multi-Module:</span>
                        <span id="isMultiModule" class="info-value"></span>
                    </div>
                    <div class="info-item">
                        <span class="info-label">Modules:</span>
                        <span id="modulesList" class="info-value"></span>
                    </div>
                </div>
                
                <div class="stats-grid">
                    <div class="stat-card stat-total">
                        <div class="stat-number" id="totalIssues">0</div>
                        <div class="stat-label">Total Issues</div>
                    </div>
                    <div class="stat-card stat-critical">
                        <div class="stat-number" id="criticalIssues">0</div>
                        <div class="stat-label">Critical Issues</div>
                    </div>
                    <div class="stat-card stat-fixable">
                        <div class="stat-number" id="autoFixableIssues">0</div>
                        <div class="stat-label">Auto-Fixable</div>
                    </div>
                </div>
            </section>

            <!-- Issues Section -->
            <section class="card" id="issues-section" style="display: none;">
                <div class="section-header">
                    <h2>🔍 Detected Issues</h2>
                    <div class="action-buttons">
                        <button onclick="fixAllAutoFixable()" class="btn btn-success" id="fixAllBtn">
                            Fix All Auto-Fixable Issues
                        </button>
                        <button onclick="fixSelected()" class="btn btn-primary" id="fixSelectedBtn">
                            Fix Selected Issues
                        </button>
                    </div>
                </div>
                
                <div class="filter-bar">
                    <label>
                        <input type="checkbox" id="filterCritical" onchange="filterIssues()"> Critical
                    </label>
                    <label>
                        <input type="checkbox" id="filterHigh" onchange="filterIssues()"> High
                    </label>
                    <label>
                        <input type="checkbox" id="filterMedium" onchange="filterIssues()"> Medium
                    </label>
                    <label>
                        <input type="checkbox" id="filterAutoFixable" onchange="filterIssues()"> Auto-Fixable Only
                    </label>
                </div>
                
                <div id="issuesList" class="issues-list"></div>
            </section>

            <!-- Fix Results Section -->
            <section class="card" id="results-section" style="display: none;">
                <h2>✅ Fix Results</h2>
                <div id="fixResults" class="fix-results"></div>
            </section>
        </main>

        <footer class="footer">
            <p>Gradle 9 Migration Helper v1.0.0 | Built with Jakarta EE & OpenLiberty</p>
        </footer>
    </div>

    <script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
