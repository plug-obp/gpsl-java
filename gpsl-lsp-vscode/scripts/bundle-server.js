#!/usr/bin/env node
/**
 * Bundle the GPSL LSP server distribution with the VS Code extension.
 * This script copies the built LSP server from gpsl-lsp/build/distributions/
 * into the extension's server/ directory.
 */

const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

// Paths
const rootDir = path.resolve(__dirname, '../..');
const lspBuildDir = path.join(rootDir, 'gpsl-lsp', 'build', 'distributions');
const extensionServerDir = path.join(__dirname, '..', 'server');

console.log('🔨 Building LSP server...');
try {
  // Build the LSP server using Gradle
  execSync('./gradlew :gpsl-lsp:installDist', {
    cwd: rootDir,
    stdio: 'inherit'
  });
} catch (error) {
  console.error('❌ Failed to build LSP server');
  process.exit(1);
}

console.log('📦 Bundling LSP server into extension...');

// Clean and create server directory
if (fs.existsSync(extensionServerDir)) {
  fs.rmSync(extensionServerDir, { recursive: true, force: true });
}
fs.mkdirSync(extensionServerDir, { recursive: true });

// Copy the installation directory
const lspInstallDir = path.join(rootDir, 'gpsl-lsp', 'build', 'install', 'gpsl-lsp');
if (!fs.existsSync(lspInstallDir)) {
  console.error('❌ LSP installation directory not found:', lspInstallDir);
  process.exit(1);
}

// Copy recursively
function copyRecursive(src, dest) {
  const stats = fs.statSync(src);
  if (stats.isDirectory()) {
    fs.mkdirSync(dest, { recursive: true });
    const entries = fs.readdirSync(src);
    for (const entry of entries) {
      copyRecursive(path.join(src, entry), path.join(dest, entry));
    }
  } else {
    fs.copyFileSync(src, dest);
  }
}

copyRecursive(lspInstallDir, extensionServerDir);

console.log('✅ LSP server bundled successfully');
console.log(`📁 Server location: ${extensionServerDir}`);
