import fs from 'fs';
import path from 'path';

// Read tokens.css
const tokensCss = fs.readFileSync('src/styles/tokens.css', 'utf-8');

console.log('--- Verifying CSS Tokens against Mockup Matrix ---');

const expectedAccents = {
  indigo: { light: '#4f46e5', dark: '#8b83ff' },
  emerald: { light: '#047857', dark: '#34d399' },
  graphite: { light: '#27272a', dark: '#d4d4d8' },
  sunset: { light: '#d9480f', dark: '#fb923c' },
};

let allPass = true;

for (const [theme, values] of Object.entries(expectedAccents)) {
  const lightRegex = new RegExp(`html\\[data-theme=["']?${theme}["']?\\][^{]*{[^}]*--accent:\\s*${values.light}`, 'i');
  const darkRegex = new RegExp(`html\\[data-mode=["']?dark["']?\\]\\[data-theme=["']?${theme}["']?\\][^{]*{[^}]*--accent:\\s*${values.dark}`, 'i');

  const lightMatch = lightRegex.test(tokensCss);
  const darkMatch = darkRegex.test(tokensCss);

  console.log(`Theme [${theme.toUpperCase()}]: Light Mode (${values.light}) -> ${lightMatch ? 'PASS' : 'FAIL'}`);
  console.log(`Theme [${theme.toUpperCase()}]: Dark Mode (${values.dark}) -> ${darkMatch ? 'PASS' : 'FAIL'}`);

  if (!lightMatch || !darkMatch) allPass = false;
}

// Check base light tokens
const hasBaseLight = tokensCss.includes('--bg: #f5f6f8') && tokensCss.includes('--surface: #ffffff');
// Check base dark tokens
const hasBaseDark = tokensCss.includes('--bg: #0d0f13') && tokensCss.includes('--surface: #15181e');

console.log(`Base Light Tokens -> ${hasBaseLight ? 'PASS' : 'FAIL'}`);
console.log(`Base Dark Tokens -> ${hasBaseDark ? 'PASS' : 'FAIL'}`);

if (!hasBaseLight || !hasBaseDark) allPass = false;

if (allPass) {
  console.log('\n>>> ALL 8 THEME/MODE TOKEN TESTS PASSED WITH 100% PARITY! <<<');
  process.exit(0);
} else {
  console.error('\n>>> TOKEN VALIDATION FAILED! <<<');
  process.exit(1);
}
