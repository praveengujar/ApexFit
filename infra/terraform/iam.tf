# Service account for Cloud Run
resource "google_service_account" "cloud_run_sa" {
  account_id   = "zyva-run-sa-${var.environment}"
  display_name = "Zyva Cloud Run Service Account (${var.environment})"
  description  = "Service account used by the Zyva API Cloud Run service"
}

# Service account for Cloud Tasks
resource "google_service_account" "cloud_tasks_sa" {
  account_id   = "zyva-tasks-sa-${var.environment}"
  display_name = "Zyva Cloud Tasks Service Account (${var.environment})"
  description  = "Service account used by Cloud Tasks to invoke task handlers"
}

# Cloud Run SA -> Cloud SQL Client
resource "google_project_iam_member" "cloud_run_sql_client" {
  project = var.project_id
  role    = "roles/cloudsql.client"
  member  = "serviceAccount:${google_service_account.cloud_run_sa.email}"
}

# Cloud Run SA -> Redis accessor (Memorystore)
resource "google_project_iam_member" "cloud_run_redis_accessor" {
  project = var.project_id
  role    = "roles/redis.editor"
  member  = "serviceAccount:${google_service_account.cloud_run_sa.email}"
}

# Cloud Run SA -> Cloud Storage access
resource "google_project_iam_member" "cloud_run_storage_access" {
  project = var.project_id
  role    = "roles/storage.objectAdmin"
  member  = "serviceAccount:${google_service_account.cloud_run_sa.email}"
}

# Cloud Run SA -> Cloud Tasks enqueuer
resource "google_project_iam_member" "cloud_run_tasks_enqueuer" {
  project = var.project_id
  role    = "roles/cloudtasks.enqueuer"
  member  = "serviceAccount:${google_service_account.cloud_run_sa.email}"
}

# Cloud Tasks SA -> Cloud Run invoker (so tasks can call back into Cloud Run)
resource "google_project_iam_member" "cloud_tasks_run_invoker" {
  project = var.project_id
  role    = "roles/run.invoker"
  member  = "serviceAccount:${google_service_account.cloud_tasks_sa.email}"
}

# Cloud Tasks SA -> Service Account Token Creator (for OIDC tokens)
resource "google_project_iam_member" "cloud_tasks_token_creator" {
  project = var.project_id
  role    = "roles/iam.serviceAccountTokenCreator"
  member  = "serviceAccount:${google_service_account.cloud_tasks_sa.email}"
}
