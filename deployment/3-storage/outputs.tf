output  "storage_id"{
    description = "Storage Account ID"
    sensitive = true
    value       = azurerm_storage_account.excellentia_storage.id
}

output "file_endpoint"{
    description = "File Storage Endpoint"
    value       = azurerm_storage_account.excellentia_storage.primary_blob_endpoint
}


output "file_host" {
    description = "File Storage Host"
    value       = azurerm_storage_account.excellentia_storage.primary_blob_host
}

output "access_key" {
    description= "Access key for storage account"
    sensitive = true
    value= azurerm_storage_account.excellentia_storage.primary_access_key
}

output "tests_url"{
    description= "URL link of the compressed tests file"
    value= azurerm_storage_blob.tests_file.url
}