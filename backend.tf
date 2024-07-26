terraform {
  backend "s3" {
    bucket     = "my-terraform-state-file-pipeline"
    region     = "ap-south-1"
    key        = "yash/terraform.tfstate"
    access_key = ""
    secret_key = ""
  }
}
