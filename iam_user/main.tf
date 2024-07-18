//login-profile and create iam user can be called as below as per need


module "iam_user" {
  source        = "./create_iam_user"
  for_each      = var.users
  iam_user_name = each.key
}
module "login" {
  source     = "./iam_login_profile"
  users      = var.users
  depends_on = [module.iam_user]
}

//module to add iam_role to iam user
module "aws_iam_role" {
  source = "./iam_role"
}

//module to add access_key to iam user
module "aws_iam_access_key" {
  source     = "./iam_access_key"
  users      = var.users
  depends_on = [module.iam_user]
}

//module to attach iam_user_policy
module "aws_iam_user_attach_policy" {
  source     = "./attach_policy"
  for_each   = var.users
  users      = each.key
  depends_on = [module.iam_user]
}

//module to create a tls certificate
module "aws_iam_user_tls_certificate" {
  source     = "./tls_certificate"
}
