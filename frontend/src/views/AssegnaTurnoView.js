import React from 'react';
import PropTypes from 'prop-types';
import SwipeableViews from 'react-swipeable-views';
import { makeStyles, useTheme } from '@material-ui/core/styles';
import AppBar from '@material-ui/core/AppBar';
import Tabs from '@material-ui/core/Tabs';
import Tab from '@material-ui/core/Tab';
import Typography from '@material-ui/core/Typography';
import Box from '@material-ui/core/Box';
import GlobalScheduleView from '../components/common/GlobalSchedulePianification';
import TemporaryDrawer from '../components/common/BottomViewAssegnazioneTurno';
import { ServizioAPI } from '../API/ServizioAPI';


function TabPanel(props) {
  const { children, value, index, ...other } = props;

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`full-width-tabpanel-${index}`}
      aria-labelledby={`full-width-tab-${index}`}
      {...other}
    >
      {value === index && (
        <Box p={3}>
          <Typography>{children}</Typography>
        </Box>
      )}
    </div>
  );
}

TabPanel.propTypes = {
  children: PropTypes.node,
  index: PropTypes.any.isRequired,
  value: PropTypes.any.isRequired,
};

function a11yProps(index) {
  return {
    id: `full-width-tab-${index}`,
    'aria-controls': `full-width-tabpanel-${index}`,
  };
}

export default function FullWidthTabs() {
  const theme = useTheme();
  const [value, setValue] = React.useState(0);
  const [allService,setService] = React.useState([])

  //Sono costretto a dichiarare questa funzione per poterla invocare in modo asincrono. 
  async function getService() {
    let serviceAPI = new ServizioAPI();
    let servizi = await serviceAPI.getService()
    setService(servizi);
  }

  //Questa funzione aggiorna lo stato del componente. Chiama in modo asincrono getService() che a sua volta contatta l'API
  //I dati scaricati dall'API verranno salvati in AllService che è una "variabile di stato" del componente
  React.useEffect(() => {
    getService();
  }, []);

  const handleChange = (event, newValue) => {
    setValue(newValue);
  };

  const handleChangeIndex = (index) => {
    setValue(index);
  };

 
  return (
    <div >
      <AppBar position="static" color="default" style={{
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              width: '100%'
            }}>
                
        <Tabs
          value={value}
          onChange={handleChange}
          indicatorColor="primary"
          textColor="primary"
          aria-label="full width tabs example"
        >
          {allService.map((service,i) => (
              <Tab label={service} {...a11yProps(i)} />
          ))}
          
        </Tabs>
      </AppBar>
      <SwipeableViews
        axis={theme.direction === 'rtl' ? 'x-reverse' : 'x'}
        index={value}
        onChangeIndex={handleChangeIndex}
      >

      {allService.map((service,i) => (
        <div>
          <TemporaryDrawer serviceName={service}></TemporaryDrawer>
          <TabPanel value={value} index={i} dir={theme.direction}>
              <GlobalScheduleView serviceName = {service} ></GlobalScheduleView>
          </TabPanel>
        </div>

          
      ))}
      
      </SwipeableViews>
    </div>
  );
}
