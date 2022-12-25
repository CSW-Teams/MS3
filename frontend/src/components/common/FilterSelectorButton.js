import React from 'react';
import { Button } from '@mui/material';
import CheckIcon from '@mui/icons-material/Check';

/** 
 * A generic filter button which selects a single attribute when clicked. 
 * This class is designed to be abstract, children should override updateLogic() method
 * in order to update filter criteria in ScheduleView state.
 * */
export class FilterSelectorButton extends React.Component{

    constructor(props){
        super(props);

        this.state = {
            
            /** The criterion selected when this button is clicked (i.e: a) */
            criterion: props.criterion,

            /** 
             * When selected, filterCriteria in ScheduleView must be changed using its callback 
             */
            updateFilterCriteria: props.updateFilterCriteriaCallback,

            /**
             * Is the criterion selected?
             * This value changes after each click of the button.
             */
            isSelected: false,

            /**
             * This function implements the updates on filter criteria specified in ScheduleView state.
             * It must take the filterCriteria object as argument and change its properties values.
             * An example: function updateLogic(filterCriteria) { filterCriteria.myAttribute = myValue; }
             */
            updateLogic: null
        }
        this.onClick = this.onClick.bind(this);
    }

    onClick(){
        
        // updates selected state of the button
        this.setState(
            {isSelected: !this.state.isSelected},
            () => {
                // updates filter criteria of the ScheduleView
                this.state.updateFilterCriteria(this.state.updateLogic);
            }
            );
    }

    render(){
        return (
            <Button variant="outlined" onClick={this.onClick} endIcon={this.state.isSelected? <CheckIcon/> : null}>{this.state.criterion}</Button>
        )
    }

}