variable "project_id" {
  description = "The GCP project ID"
  type        = string
}

variable "region" {
  description = "The GCP region for resource deployment"
  type        = string
  default     = "us-central1"
}

variable "environment" {
  description = "Deployment environment"
  type        = string
  default     = "dev"

  validation {
    condition     = contains(["dev", "staging", "prod"], var.environment)
    error_message = "Environment must be one of: dev, staging, prod."
  }
}

variable "db_tier" {
  description = "Cloud SQL machine tier. Use db-f1-micro for dev, db-custom-2-7680 for staging, db-custom-4-15360 for prod."
  type        = string
  default     = "db-f1-micro"
}

variable "redis_tier" {
  description = "Memorystore Redis service tier. BASIC for dev, STANDARD_HA for prod."
  type        = string
  default     = "BASIC"

  validation {
    condition     = contains(["BASIC", "STANDARD_HA"], var.redis_tier)
    error_message = "Redis tier must be BASIC or STANDARD_HA."
  }
}

variable "domain_name" {
  description = "Custom domain name for the Cloud Run service (e.g. api.zyva.app)"
  type        = string
  default     = ""
}
