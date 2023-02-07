package com.zbutwialypiernik.scrappify.database

import com.github.tminglei.slickpg.{ExPostgresProfile, PgSearchSupport}

trait TextSearchPostgresProfile extends ExPostgresProfile with PgSearchSupport {

  override val api = TextSearchAPI
  object TextSearchAPI extends API
    with SearchImplicits
    with SearchAssistants
}

object TextSearchPostgresProfile extends TextSearchPostgresProfile
