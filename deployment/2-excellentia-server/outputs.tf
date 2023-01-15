output "kube_config" {
  value     = azurerm_kubernetes_cluster.backend.kube_config_raw
  sensitive = true
}

output "namespace_id" {
  description = "Namespace ID"
  value       = module.basic_setup.namespace_id
}
