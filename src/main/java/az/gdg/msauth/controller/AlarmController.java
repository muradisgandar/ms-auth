package az.gdg.msauth.controller;

import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@CrossOrigin(exposedHeaders = "Access-Control-Allow-Origin")
@RequestMapping("/alarm")
public class AlarmController {

    private static final Logger logger = LoggerFactory.getLogger(AlarmController.class);


    @ApiOperation(value = "Method will be called by ms-alarm")
    @GetMapping
    public void alarm() {
        logger.info("ActionLog.ms-auth.start");
    }
}
