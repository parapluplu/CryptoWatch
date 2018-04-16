package de.paraplu.cryptocurrency.aphrodite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.paraplu.cryptocurrency.domain.mongodb.pojo.trigger.TriggerEvent;
import de.paraplu.cryptocurrency.domain.mongodb.repository.TriggerEventRepository;

@CrossOrigin
@RestController
@RequestMapping("/triggerEvents")
public class TriggerEventController {

    @Autowired
    private TriggerEventRepository repository;

    // @Autowired
    // PagedResourcesAssembler<MyDTO> resourceAssembler;

    @GetMapping("/all")
    public Page<TriggerEvent> findAll(Pageable pageable) {

        // spring doesn't spoil your sort here ...
        Page<TriggerEvent> page = repository.findAll(pageable);

        // optionally, apply projection
        // to return DTO/specifically loaded Entity objects ...
        // return type would be then PagedResources<Resource<MyDTO>>
        // return resourceAssembler.toResource(page.map(...))

        return page;
    }

}