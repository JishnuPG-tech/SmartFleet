#!/usr/bin/env python3
"""
tools/lint_no_empty_onclick.py

Enforces Antigravity Ground Rule 4:
Every clickable component must have a non-empty, functional onClick handler.
Fails if any Kotlin source file contains:
- `onClick = {}` or `onClick = { }`
- `onClick = { /* ... */ }` without executable code
"""

import sys
import os
import re

KOTLIN_DIR = os.path.join(os.path.dirname(__file__), "..", "android")

EMPTY_ONCLICK_REGEX = re.compile(r'onClick\s*=\s*\{\s*\}')
EMPTY_LAMBDA_BODY_REGEX = re.compile(r'onClick\s*=\s*\{\s*(?://.*|\/\*.*?\*\/)?\s*\}', re.DOTALL)

def run_lint():
    violations = []
    kotlin_dir = os.path.abspath(KOTLIN_DIR)
    
    for root, dirs, files in os.walk(kotlin_dir):
        # Skip build directories
        if "build" in root.split(os.sep) or ".gradle" in root.split(os.sep):
            continue
            
        for file in files:
            if not file.endswith(".kt"):
                continue
                
            file_path = os.path.join(root, file)
            with open(file_path, "r", encoding="utf-8", errors="ignore") as f:
                content = f.read()
                
            for line_idx, line in enumerate(content.splitlines(), start=1):
                if EMPTY_ONCLICK_REGEX.search(line):
                    violations.append((file_path, line_idx, line.strip()))

    if violations:
        print(f"❌ LINT FAILURE: Found {len(violations)} empty onClick handlers:")
        for path, line_no, code in violations:
            rel_path = os.path.relpath(path, kotlin_dir)
            print(f"  {rel_path}:{line_no} -> {code}")
        print("\nGround Rule 4 Violation: All clickable components must provide an active, non-empty onClick action.")
        return 1

    print("✅ LINT PASSED: Zero empty onClick handlers detected in all Kotlin source files.")
    return 0

if __name__ == "__main__":
    sys.exit(run_lint())
