#!/bin/bash

while true; do
    read -p "Enter the task number: " task_number
    if [ -n "$task_number" ]; then
        task_number=$(printf "%04d" "$task_number")
        break
    fi
    echo "Task number is required."
done

read -p "Enter the index (optional, press Enter to use 00): " index_number
if [ -z "$index_number" ]; then
    index_number="00"
else
    index_number=$(printf "%02d" "$index_number")
fi

while true; do
    read -p "Choose the type (DDL, DML): " type
    type=$(echo "$type" | tr '[:lower:]' '[:upper:]')
    if [[ "$type" == "DDL" || "$type" == "DML" ]]; then
        break
    fi
    echo "Type must be either DDL or DML."
done

while true; do
    read -p "Enter the description: " description
    if [ -n "$description" ]; then
        description=$(echo "$description" | tr '[:upper:]' '[:lower:]' | tr ' ' '_')
      break
    fi
    echo "Description is required."
done

current_date=$(date +"%Y.%m.%d")
filename="V${current_date}.${task_number}.${index_number}__${type}_${description}.sql"
filepath="src/main/resources/db/migrations/$filename"
touch $filepath

current_dir=$(dirname "$(realpath "$0")")
full_filepath=$(echo "$current_dir/$filepath" | sed 's/ /%20/g')
if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "cygwin" ]]; then
  full_filepath=$(echo "$full_filepath" | sed 's|^/c|/C:|')
fi
echo "Generated migration file://$full_filepath"
