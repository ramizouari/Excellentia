# Pulling information about the resource group
data "azurerm_resource_group" "academic" {
  name = var.resource_group_name
}

resource "azurerm_kubernetes_cluster" "backend" {
  name                = "excellentia-aks"
  location            = data.azurerm_resource_group.academic.location
  resource_group_name = data.azurerm_resource_group.academic.name
  dns_prefix          = "excellentia"
  sku_tier            = "Free"

  default_node_pool {
    name       = "default"
    node_count = 1
    vm_size    = "standard_ds2_v2"
  }

  identity {
    type = "SystemAssigned"
  }

  tags = {
    Environment = "Development"
  }
}

locals {
  host                   = azurerm_kubernetes_cluster.backend.kube_config.0.host
  username               = azurerm_kubernetes_cluster.backend.kube_config.0.username
  password               = azurerm_kubernetes_cluster.backend.kube_config.0.password
  client_certificate     = base64decode(azurerm_kubernetes_cluster.backend.kube_config.0.client_certificate)
  client_key             = base64decode(azurerm_kubernetes_cluster.backend.kube_config.0.client_key)
  cluster_ca_certificate = base64decode(azurerm_kubernetes_cluster.backend.kube_config.0.cluster_ca_certificate)
}

provider "kubernetes" {
  alias                  = "excellentia"
  host                   = local.host
  username               = local.username
  password               = local.password
  client_certificate     = local.client_certificate
  client_key             = local.client_key
  cluster_ca_certificate = local.cluster_ca_certificate
}


provider "helm" {
  kubernetes {
    host                   = local.host
    username               = local.username
    password               = local.password
    client_certificate     = local.client_certificate
    client_key             = local.client_key
    cluster_ca_certificate = local.cluster_ca_certificate
  }
}


module "basic_setup" {
  source = "./modules/basic_setup"
  providers = {
    kubernetes = kubernetes.excellentia
  }
  environment = "dev"
}
