package org.csu.gateway;

import org.csu.utils.TokenTools;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GatewayServiceApplicationTests {
    @Autowired
    private TokenTools tokenTools;
/*    @Test
    void generateShortToken() {
        String token = tokenTools.createShortToken(123456);
        System.out.println(token);
    }*/

}
