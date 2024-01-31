package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Adding a song."
    request {
        url "/songs"
        method POST()
        headers {
            contentType applicationJson()
        }
        body(
                name: "name",
                artist: "artist",
                album: "album",
                duration: "length",
                year: "year",
                resourceId: 1L
        )
    }
    response {
        status OK()
        headers {
            contentType applicationJson()
        }
        body(1)
    }
}