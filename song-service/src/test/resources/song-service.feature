Feature: post new Song

  Scenario: client makes call to POST /songs
    When the client calls song service
    Then the client receives status code of 200
    And the client receives 1 of created song