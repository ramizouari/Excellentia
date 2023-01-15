resource "kubernetes_namespace" "excellentia" {
  metadata {
    labels = {
      environment = var.environment
    }

    generate_name = "gl5-"
  }
}
