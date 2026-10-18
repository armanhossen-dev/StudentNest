# Useful Git Commands & Workflows

A practical reference guide for Git operations in this project. All commands, commit messages, and workflows are kept up to date here.

---

## 1. Current Repository Status & Commits

### To Check Status:
```bash
git status
```

### To Commit Newly Added `v2/`, `Screenshot/v2/` & Docs:
```bash
# 1. Stage changes
git add .

# 2. Commit with message
git commit -m "feat(v2): add v2 build, screenshots, and updated documentation"

# 3. Push to GitHub
git push origin main
```

---

## 2. How Deleted Folders / Files Were Restored from Git History

When you delete files or folders and commit the change, Git **never truly destroys them**—they remain stored inside the repository commit tree.

### Step 1: Find the Commit Before the Deletion
```bash
# View recent commits in compact format
git log --oneline -n 5

# Or check specifically which commit deleted a folder/file
git log --diff-filter=D --summary
```

Example log:
```text
8624620 (HEAD -> main) release(v1): initial stable JavaFX release and repository cleanup
38794a6 Update README.md   <--- (Here, previous files existed!)
```

### Step 2: Restore the Specific Folder or File from That Commit
Use `git checkout` (or `git restore`) with the previous commit hash and the target path:
```bash
# Syntax: git checkout <commit-hash> -- <path>
git checkout 38794a6 -- "v4.0"
```

*Or using modern `git restore` syntax:*
```bash
# Syntax: git restore --source=<commit-hash> <path>
git restore --source=38794a6 v4.0
```

---

## 3. Discarding, Undoing & Staging Management

### Restore a modified or deleted file that has NOT been committed yet:
```bash
# Discard changes to a specific file
git restore filename.txt

# Discard changes to an entire directory
git restore path/to/folder/
```

### Unstage files (undo `git add`):
```bash
# Unstage a single file
git restore --staged filename.txt

# Unstage everything back to working directory
git restore --staged .
```

### Discard the last commit but keep your local edits:
```bash
git reset --soft HEAD~1
```

---

## 4. How to Run Each Version

### Running `v1`:
```bash
cd /Users/armanhossenripon/Documents/GitHub/StudentNest/v1
./mvnw javafx:run
```

### Running `v2`:
```bash
cd /Users/armanhossenripon/Documents/GitHub/StudentNest/v2
./mvnw javafx:run
```
