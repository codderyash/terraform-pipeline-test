variable "access_key" {
  description = "access-Key of aws"
  type = string
}

variable "secret_key" {
  description = "secret key of aws"
  type = string
}

variable "users" {
   type = map(map(string))
}
