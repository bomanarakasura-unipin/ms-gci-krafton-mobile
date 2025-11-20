#!/bin/bash

# Exit on error
set -e

kebab_to_pascal() {
  local input="$1"

  echo "$input" | sed -r 's/(^|-)([a-z])/\U\2/g'
}

extract_gci_parent_name() {
  local input="$1"

  echo "$input" | sed -r 's/ms-gci-//'
}

create_package_name() {
  local input="$1"

  echo "$input" | sed -r 's/-//g'
}

main() {
  echo "Starting to initiating GCI project..."

  # Get repositoryu name
  REPO_NAME="${GITHUB_REPOSITORY##*/}"

  # Create all string replacement
  local package_name=$(create_package_name "$REPO_NAME")
  local parent_name=$(extract_gci_parent_name "$REPO_NAME")
  local integration_name=$(kebab_to_pascal "$parent_name")

  echo "Package name : $package_name"
  echo "Parent name : $parent_name"
  echo "Integration name : $integration_name"

  # Renaming directory
  # Rename project main directoryu
  # Sort in reverse order to avoind renaming parent name before children
  find . -type d -name "*gci-parent-name*" ! -path "./git/*" | sort -r | while read -r dir; do
    # Get parent directory
    parent=$(dirname "$dir")
    # Get folder name
    foldername=$(basename "$dir")

    # Replace dir name
    new_foldername=${foldername//"gci-parent-name"/"$package_name"}
    new_dir="$parent/$new_foldername"

    if [ "$dir" != "$new_path" ]; then
      echo "Renaming directory $dir => $new_dir"

      mv "$dir" "$new_dir"
    fi
  done

  # Rename project main directoryu
  # Sort in reverse order to avoind renaming parent name before children
  find . -type d -name "*gci-parent-name*" ! -path "./git/*" | sort -r | while read -r dir; do
    # Get parent directory
    parent=$(dirname "$dir")
    # Get folder name
    foldername=$(basename "$dir")

    # Replace dir name
    new_foldername=${foldername//"gci-parent-name"/"$package_name"}
    new_dir="$parent/$new_foldername"

    if [ "$dir" != "$new_path" ]; then
      echo "Renaming directory $dir => $new_dir"

      mv "$dir" "$new_dir"
    fi
  done

  # Renaming all files
  find . -type f -name "*integration-name*" ! -path "./git/*" | while read -r file; do
    # Get parent filename
    parent=$(dirname "$file")
    # Get file name
    filename=$(basename "$file")

    # Replace
    new_filename=${filename//"integration-name"/"$integration_name"}
    new_path="$parent/$new_filename"

    if [ "$file" != "$new_filename" ]; then
      echo "Renaming filename $file => $new_path"

      mv "$file" "$new_path"
    fi
  done

  # Start replacement
  echo "Starting replacement in all files..."
  find . -type f ! -path "*/.git/*" | while read -r game_file; do
    # Check for gci-parent-name
    if grep -q "gci-parent-name" "$game_file" 2>/dev/null; then
      echo "Updating gci-parent-name on $game_file"
      sed -i "s/gci-parent-name/$package_name/g" "$game_file"
    fi

    # Check for gci-parent-name
    if grep -q "parent-name" "$game_file" 2>/dev/null; then
      echo "Updating parent-name on $game_file"
      sed -i "s/parent-name/$parent_name/g" "$game_file"
    fi

  done

  echo "Make a commit"
  git config user.name github-automation-bomsiwor[bot]
  git config user.email github-automation@unipin.bot

  echo "Stage changes"
  git add -A

  echo "Commit"
  git commit -m "feat: initializing $REPO_NAME project"

  echo "Pushing to main branch"
  git push origin main

  echo "Repository completely setup"
}

main
