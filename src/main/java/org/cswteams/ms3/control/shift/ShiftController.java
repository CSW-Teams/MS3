package org.cswteams.ms3.control.shift;

import org.cswteams.ms3.dao.ShiftDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShiftController implements IShiftController {

    @Autowired
    ShiftDAO shiftDAO;

}
