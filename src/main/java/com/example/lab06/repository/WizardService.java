package com.example.lab06.repository;

import com.example.lab06.pojo.Wizard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WizardService {
    @Autowired
    private WizardRepository repository;

    public List<Wizard> getWizards() {
        return repository.findAll();
    }
    public Wizard addWizard(Wizard w) {
        w.set_id(null);
        return repository.save(w);
    }
    public Wizard updateWizard(Wizard w) {
        return repository.save(w);
    }
    public boolean deleteWizard(Wizard w) {
        repository.deleteById(w.get_id());
        return true;
    }

}
