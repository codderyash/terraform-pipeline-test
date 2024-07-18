terraform {
  required_providers {

    pgp = {
      source = "ekristen/pgp"
    }
  }
}

resource "pgp_key" "user_login_key" {
  for_each = var.users
  name     = each.value.name
  email    = each.value.email
  comment  = "PGP Key for ${each.value.name}"
}


resource "aws_iam_user_login_profile" "user_login" {
  for_each = var.users

  user                    = each.key
  pgp_key                 = pgp_key.user_login_key[each.key].public_key_base64
  password_reset_required = true
  depends_on              = [pgp_key.user_login_key]
}

data "pgp_decrypt" "user_password_decrypt" {
  for_each            = var.users
  ciphertext          = aws_iam_user_login_profile.user_login[each.key].encrypted_password
  ciphertext_encoding = "base64"
  private_key         = pgp_key.user_login_key[each.key].private_key
}
