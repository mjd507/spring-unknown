package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
	request {
		method 'GET'
		url '/account/10'
		headers {
			header('X-API-Version', '1.0')
		}
	}
	response {
		status OK()
		body({
			id: "12"
			name: "default"
		})
		headers {
			contentType('application/json')
		}
	}
}