data "azurerm_resource_group" "academic" {
  name = var.resource_group_name
}

resource "azurerm_mssql_server" "excellentia_sql_server" {
  name                         = "excellentia-mssql-server"
  resource_group_name          = data.azurerm_resource_group.academic.name
  location                     = data.azurerm_resource_group.academic.location
  version                      = "12.0"
  administrator_login          = "excellentia"
  administrator_login_password = "gl5@12345678"
  minimum_tls_version          = "1.2"
  public_network_access_enabled = true


  tags = {
    environment = "dev"
  }
}

resource "azurerm_mssql_database" "excellentia_db" {
  name                = "excellentia"
  server_id         = azurerm_mssql_server.excellentia_sql_server.id
  collation      = "SQL_Latin1_General_CP1_CI_AS"
  license_type   = "LicenseIncluded"
#  max_size_gb    = 4
#  read_scale     = true
  sku_name       = "S0"
  tags = {
    environment = "dev"
  }
}