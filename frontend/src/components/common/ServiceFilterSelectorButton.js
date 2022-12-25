import { FilterSelectorButton } from './FilterSelectorButton';

export class ServiceFilterSelectorButton extends FilterSelectorButton{
    constructor(props){
        super(props);
        this.updateLogic = this.updateLogic.bind(this);
    }

    componentDidMount(){
        
        // we define how we update filter criteria in response to the selection of
        // the service corresponding to this button
        this.setState({updateLogic: this.updateLogic});
    }

    updateLogic(filterCriteria){
        
        // we add|remove the service to|from the set of selected services
        this.state.isSelected? filterCriteria.services.add(this.state.criterion) : filterCriteria.services.delete(this.state.criterion);
    }
    
    render(){
        return super.render();
    }
}