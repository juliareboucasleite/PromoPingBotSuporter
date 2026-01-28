import os
import re

def remove_todo_comments(file_path):
    """Remove TODO comments related to Button/ActionRow"""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        original = content
        
        # Remove TODO comments about Button/ActionRow
        content = re.sub(r'\s*//\s*TODO:\s*Button/ActionRow.*?\n', '', content, flags=re.MULTILINE)
        content = re.sub(r'\s*//\s*TODO:\s*Implementar.*?\n', '', content, flags=re.MULTILINE)
        
        # Remove commented Button/ActionRow code blocks
        content = re.sub(r'\s*//\s*Button.*?\n', '', content, flags=re.MULTILINE)
        content = re.sub(r'\s*//\s*ActionRow.*?\n', '', content, flags=re.MULTILINE)
        content = re.sub(r'\s*//\s*StringSelectMenu.*?\n', '', content, flags=re.MULTILINE)
        
        # Remove lines that are just commented code with Button/ActionRow
        lines = content.split('\n')
        new_lines = []
        i = 0
        while i < len(lines):
            line = lines[i]
            # Skip lines that are commented Button/ActionRow code
            if re.match(r'\s*//\s*(Button|ActionRow|StringSelectMenu|\.setComponents)', line):
                i += 1
                continue
            # Skip lines that are just "// Not available"
            if re.match(r'\s*//\s*Not available', line):
                i += 1
                continue
            new_lines.append(line)
            i += 1
        
        content = '\n'.join(new_lines)
        
        # Clean up multiple empty lines
        content = re.sub(r'\n{3,}', '\n\n', content)
        
        if content != original:
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write(content)
            return True
        return False
    except Exception as e:
        print(f"Error processing {file_path}: {e}")
        return False

# Find all Java files with TODO comments
files_to_fix = []
for root, dirs, files in os.walk('src'):
    for file in files:
        if file.endswith('.java'):
            file_path = os.path.join(root, file)
            try:
                with open(file_path, 'r', encoding='utf-8') as f:
                    content = f.read()
                    if 'TODO:' in content or 'Button/ActionRow' in content:
                        files_to_fix.append(file_path)
            except:
                pass

print(f"Found {len(files_to_fix)} files to clean")
for file_path in files_to_fix:
    if remove_todo_comments(file_path):
        print(f"Cleaned: {file_path}")
    else:
        print(f"No changes: {file_path}")

print("Done!")
