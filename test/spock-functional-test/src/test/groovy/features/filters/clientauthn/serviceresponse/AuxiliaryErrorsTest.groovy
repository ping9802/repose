package features.filters.clientauthn.serviceresponse

import features.filters.clientauthn.IdentityServiceResponseSimulator
import framework.ReposeValveTest
import org.rackspace.gdeproxy.Deproxy
import org.rackspace.gdeproxy.MessageChain
import spock.lang.Unroll

class AuxiliaryErrorsTest extends ReposeValveTest {

    def static originEndpoint
    def static identityEndpoint

    static IdentityServiceResponseSimulator fakeIdentityService

    def setupSpec() {
        deproxy = new Deproxy()

        repose.applyConfigs("features/filters/clientauthn/common")
        repose.start()

        originEndpoint = deproxy.addEndpoint(properties.getProperty("target.port").toInteger(), 'origin service')
        fakeIdentityService = new IdentityServiceResponseSimulator()
        identityEndpoint = deproxy.addEndpoint(properties.getProperty("identity.port").toInteger(),
                'identity service', null, fakeIdentityService.handler)

    }

    def setup() {

    }

    def cleanupSpec() {
        if (deproxy) {
            deproxy.shutdown()
        }
        repose.stop()
    }

    @Unroll("Identity Service Admin Code: #adminCode Validate Code: #validateCode Group Code: #groupCode")
    def "When the identity service endpoint returns failed or unexpected responses"() {

        given: "When retrieving and admin token is broken"
        fakeIdentityService.adminCode = adminCode
        fakeIdentityService.validateCode = validateCode
        fakeIdentityService.groupCode = groupCode


        when: "User sends a request through repose"
        MessageChain mc = deproxy.makeRequest(reposeEndpoint, 'GET', ['X-Auth-Token': fakeIdentityService.client_token])


        then: "User should receive a " + expectedCode + "response"
        mc.receivedResponse.code == "500"
        mc.sentRequest.he

        where:
        adminCode | validateCode | groupCode | expectedCode
        400       | 401          | 401       | "500"
        401       | 401          | 401       | "500"
        402       | 401          | 401       | "500"
        403       | 401          | 401       | "500"
        404       | 401          | 401       | "500"
        413       | 401          | 401       | "500"
        429       | 401          | 401       | "500"
        500       | 401          | 401       | "500"
        501       | 401          | 401       | "500"
        502       | 401          | 401       | "500"
//        503       | 401          | 401       | "500"



    }

}
