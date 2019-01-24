package paymentmicroservice.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import paymentmicroservice.Models.Summary;
import paymentmicroservice.Repository.SummaryRepo;

import java.util.Optional;

@Service
public class SummaryService {

    @Autowired
    SummaryRepo summaryRepo;
    public void Save(Summary summary)
    {
         summaryRepo.save(summary);
    }

    public float getAmount(String orderId)
    {
        Optional op = summaryRepo.findById(orderId);
        if(op.isPresent())
        {
            Summary summary = (Summary)op.get();
            return summary.getAmount();
        }
        return -1;
    }
}
