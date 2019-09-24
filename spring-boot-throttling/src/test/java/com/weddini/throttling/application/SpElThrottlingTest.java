package com.weddini.throttling.application;

import com.weddini.throttling.ThrottlingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpElThrottlingTest {

    @Autowired private DemoService demoService;
    @Autowired private TestDateProvider dateProvider;

    @Test
    public void computeWithSpElThrottling() {
        Model model1 = new Model("Misha");
        Model model2 = new Model("Vasya");

        demoService.computeWithSpElThrottling(model1);
        demoService.computeWithSpElThrottling(model1);
        demoService.computeWithSpElThrottling(model1);

        assertThatThrownBy(() -> demoService.computeWithSpElThrottling(model1))
            .isInstanceOf(ThrottlingException.class);

        demoService.computeWithSpElThrottling(model2);
        demoService.computeWithSpElThrottling(model2);
        demoService.computeWithSpElThrottling(model2);

        assertThatThrownBy(() -> demoService.computeWithSpElThrottling(model2))
            .isInstanceOf(ThrottlingException.class);

        dateProvider.addMillis(60 * 1000 + 10);

        demoService.computeWithSpElThrottling(model1);
        demoService.computeWithSpElThrottling(model1);
        demoService.computeWithSpElThrottling(model1);

        assertThatThrownBy(() -> demoService.computeWithSpElThrottling(model1))
            .isInstanceOf(ThrottlingException.class);

        demoService.computeWithSpElThrottling(model2);
        demoService.computeWithSpElThrottling(model2);
        demoService.computeWithSpElThrottling(model2);

        assertThatThrownBy(() -> demoService.computeWithSpElThrottling(model2))
            .isInstanceOf(ThrottlingException.class);
    }
}
