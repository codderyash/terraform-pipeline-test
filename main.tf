terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
   
  } 
}

# Configure the AWS Provider
provider "aws" {
  region = "ap-south-1"
  access_key = var.access_key
  secret_key = var.secret_key
}  

module "iam_user_all_rules_violation" {
  source = "./iam_user"
  access_key = var.access_key
  secret_key = var.secret_key
  users = var.users
}
