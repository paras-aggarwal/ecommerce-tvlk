package paymentmicroservice.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import paymentmicroservice.Models.*;
import paymentmicroservice.Repository.CodSupportRepo;
import paymentmicroservice.Repository.PaymentOptionRepo;
import paymentmicroservice.Repository.SummaryRepo;
import paymentmicroservice.Validation.BasicValidation;

import java.util.*;

@Service
public class PaymentOptionService {

    @Autowired
    PaymentOptionRepo paymentOptionRepo;
    @Autowired
    CodSupportRepo codSupportRepo;
    @Autowired
    SummaryService summaryService;
    @Autowired
    BasicValidation basicValidation;
    private static final String cod = "COD";
    private static final float codMaxBase = 50000;
    private static final float emiMinBase=3000;
    private static final String emi="EMI";

      //Controller Function
    public ResponseEntity<CustomResponse> getOptions(Order order)
    {
        try {
            String orderId = order.orderId;
            if (!basicValidation.validateString(orderId)) {
                return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse(400, "OrderId is not Valid", null));
            }
            CheckOut checkOut = getAllDetails(orderId);
            List<String> productIds = checkOut.getProductIds();
            float amount = checkOut.getAmount();
            if (!(basicValidation.validateAmount(amount) && basicValidation.validateList(productIds))) {
                return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse(400, "Incomsistent Data", null));
            }
            List<String> paymentOptions = getPaymentOption(productIds, amount);
            Summary summary = new Summary(orderId, null, null, null, amount, null);
            summary=summaryService.save(summary);
            if(summary==null)
                throw new NullPointerException();
            Response response = new Response();
            response.options = paymentOptions;
            response.orderId = orderId;
            return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse(200, "OK", response));
        }
        catch (NullPointerException e)
        {
            return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse(400, "Couldn't Save Data", null));
        }
    }
    // Controller Function
    public ResponseEntity<CustomResponse> validate(PaymentOptionSelected paymentOptionSelected)
    {
        try {
            String optionSelected = paymentOptionSelected.optionSelected;
            String orderId = paymentOptionSelected.orderId;
            if ((basicValidation.validateString(orderId) && basicValidation.validateString(optionSelected))) {
                float amount = summaryService.getAmount(paymentOptionSelected.orderId);
                CheckOut checkOut = getAllDetails(orderId);
                List<String> productIds = checkOut.getProductIds();
                if (basicValidation.validateList(productIds)&&basicValidation.validateAmount(amount)) {
                    List<String> paymentOptions = getPaymentOption(productIds, amount);
                    List<String> temp = new ArrayList<>();
                    Response response = new Response();
                    response.orderId = paymentOptionSelected.orderId;
                    if (paymentOptions.contains(optionSelected)) {
                        temp.add(optionSelected);
                        response.options = temp;
                        Summary summary = new Summary(paymentOptionSelected.orderId, optionSelected, null, null, amount, null);
                        summary=summaryService.save(summary);
                        if(summary!=null)
                        return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse(200, "OK", response));
                    }
                }
            }

            return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse(400, "You have selected an incorrect option", new Response()));
        }
        catch (NullPointerException e){
            return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse(400, "Something Went Wrong", new Response()));
        }
    }

     //
    public List<String> getPaymentOption(List<String>productIds,float amount)
    {
        List<String> paymentOptions=paymentOptionRepo.getOptions();
        if(hasCOD(paymentOptions)&& (codMaxBase<amount||codNotAvailable(productIds)))
        {
            paymentOptions.remove(cod);
        }
        if(amount<emiMinBase&&paymentOptions.contains(emi))
        {
            paymentOptions.remove(emi);
        }
        return paymentOptions;
    }

    public boolean hasCOD(List<String>paymentOptions)
    {
        for(String paymentOption:paymentOptions)
        {
            if(paymentOption.equalsIgnoreCase(cod))
                return true;
        }
        return false;
    }


    public boolean codNotAvailable(List<String>productIds)
    {
        for (String id:productIds)
        {
            if(codSupportRepo.findById(id).isPresent())
                return true;
        }
        return false;
    }

    public CheckOut getAllDetails(String orderId)
    {
        try {
            final String uri = "http://d034eb08.ngrok.io/orderDetails/"+orderId;
            Order order = new Order();
            order.orderId = orderId;
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(order.orderId);
            String response = restTemplate.getForObject(uri,String.class);
            JSONObject obj = new JSONObject(response);
            JSONObject arr = obj.getJSONObject("responseData");
            ObjectMapper mapper = new ObjectMapper();
            CheckOut checkOut = mapper.readValue(arr.toString(),CheckOut.class);
            System.out.println(checkOut.getProductIds());
            return checkOut;

        }
        catch (Exception e){
            return new CheckOut();
        }

    }

}
