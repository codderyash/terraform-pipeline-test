#!/bin/bash

declare -a modules=("iam_user_all_rules_violation")

run_terraform() {
    local module_name=$1
    local action=$2
    if [ ! -f ./.terraform.lock.hcl ]
    then
    terraform init
    fi
    
    if [ "$action" == "apply" ]; then
        echo "Running terraform apply for module: $module_name"
        terraform apply -auto-approve -target=module.$module_name
    elif [ "$action" == "destroy" ]; then
        echo "Running terraform destroy for module: $module_name"
        terraform destroy -auto-approve -target=module.$module_name
    else
        echo "Running terraform plan for module: $module_name"
        terraform plan  -target=module.$module_name
    fi
}

# Function to validate if a module exists
validate_module() {
    local module=${modules[$1]}
    for m in "${modules[@]}"; do
        if [ "$module" == "$m" ]; then
            return 0
        fi
    done
    return 1
}


for index in "${!modules[@]}"; do
    echo "Module $index: ${modules[$index]}"
done

read -p "Enter module number to run (comma-separated, or 'all' for all modules): " input_modules

# Convert comma-separated input to array
IFS=', ' read -r -a selected_modules <<< "$input_modules"

read -p "Enter action (apply / destroy / plan / refresh): " action

for module in "${selected_modules[@]}"; do
    if [ "$module" == "all" ]; then
        for m in "${modules[@]}"; do
            run_terraform "$m" "$action"
        done
    elif validate_module "$module"; then
        run_terraform "${modules[$module]}" "$action"
    else
        echo "Module '$module' is not valid or does not exist."
    fi
done

