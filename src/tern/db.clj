(ns tern.db)

(defprotocol Migrator
  "Protocol that must be extended by all Migrator instances.
  Provides the base level of functionality required by `tern`."

  (init
    [this]
    "Perform any setup required for tern to work, such as the creation
    of the schema_versions table.")

  (version
    [this]
    "Return the current version of the database.")

  (migrate
    [this version commands]
    "Apply the given migration and update the schema_versions table accordingly."))
