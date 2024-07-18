
resource "aws_iam_user_policy" "example_policy" {
  name   = "example-policy"
  user   = var.users
  policy = <<EOF
  {
    "Version": "2012-10-17",
    "Statement": [
      {
        "Effect": "Allow",
        "Action": "*",
        "Resource": "*"
      }
    ]
  }
  EOF
}
resource "aws_iam_user_policy_attachment" "example_attachment" {
  user       = var.users
  policy_arn = "arn:aws:iam::aws:policy/AmazonS3FullAccess"
}

resource "aws_iam_user_policy_attachment" "admin_attachment" {
  user       = var.users
  policy_arn = "arn:aws:iam::aws:policy/AdministratorAccess"
}
