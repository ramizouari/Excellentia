output "mssql-dns"{
  value= azurerm_mssql_server.excellentia_sql_server.fully_qualified_domain_name
  description= "DNS that should be used to connect to MS-SQL Server"
}