# This example creates a self-signed certificate,
# and uses it to create an AWS IAM Server certificate.

resource "tls_private_key" "example" {
  algorithm = "ECDSA"
}

resource "tls_self_signed_cert" "example" {
  private_key_pem = tls_private_key.example.private_key_pem

  # Certificate expires after 1 hours.
  validity_period_hours = 1

  # Reasonable set of uses for a server SSL certificate.
  allowed_uses = [
    "key_encipherment",
    "digital_signature",
    "server_auth",
  ]

  dns_names = ["example.com", "example.net"]

  subject {
    common_name  = "example.com"
    organization = "ACME Examples, Inc"
  }
  depends_on = [ tls_private_key.example ]
}

# For example, this can be used to populate an AWS IAM server certificate.
resource "aws_iam_server_certificate" "example" {
  name             = "some_test_cert"
  certificate_body = tls_self_signed_cert.example.cert_pem
  private_key      = tls_private_key.example.private_key_pem
  depends_on = [ tls_self_signed_cert.example ]
}
