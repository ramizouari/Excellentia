data "azurerm_resource_group" "academic" {
  name = var.resource_group_name
}

resource "azurerm_storage_account" "excellentia_storage" {
  name                     = "excellentiastorage"
  resource_group_name      = data.azurerm_resource_group.academic.name
  location                 = data.azurerm_resource_group.academic.location
  account_tier             = "Standard"
  account_replication_type = "LRS"
  account_kind = "BlobStorage"
  shared_access_key_enabled = true

  tags = {
    environment = "dev"
  }
}


resource "azurerm_storage_container" "excellentia" {
  name                  = "tests"
  storage_account_name  = azurerm_storage_account.excellentia_storage.name
  container_access_type = "blob"
}

resource "azurerm_storage_blob" "tests_file" {
  name                   = "tests.7z"
  storage_account_name   = azurerm_storage_account.excellentia_storage.name
  storage_container_name = azurerm_storage_container.excellentia.name
  type                   = "Block"
  source                 = "tests.7z"
}