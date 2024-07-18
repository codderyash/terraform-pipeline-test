resource "aws_iam_access_key" "access_key" {
  for_each = var.users
  user     = each.key
}
