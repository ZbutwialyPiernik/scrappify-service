package com.zbutwialypiernik.scrappify

import com.zbutwialypiernik.scrappify.fixture.CommonDataGenerators
import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

trait BaseUnitTest extends AsyncWordSpec
  with Matchers
  with GivenWhenThen
  with AsyncMockFactory
  with CommonDataGenerators {

}
