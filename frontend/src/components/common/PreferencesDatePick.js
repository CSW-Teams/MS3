import {Calendar, DateObject} from "react-multi-date-picker";
import ToolBar from "react-multi-date-picker/plugins/toolbar";
import Button from "@mui/material/Button";
import React, {useRef, useState} from "react";
import {DesiderateAPI} from "../../API/DesiderataAPI"
import {toast, ToastContainer} from "react-toastify";
import {MDBCard} from "mdb-react-ui-kit";
import {t} from "i18next";
import {panic} from "./Panic";

const months =   [t("January"),
  t("February"),
  t("March"),
  t("April"),
  t("May"),
  t("June"),
  t("July"),
  t("August"),
  t("September"),
  t("October"),
  t("November"),
  t("December")]
const weeksName= [  t("Sun"),
  t("Mon"),
  t("Tue"),
  t("Wed"),
  t("Thu"),
  t("Fri"),
  t("Sat")]

function DateSelectSlots({props}) {

  let id = localStorage.getItem("id");
  let desiderataApi = new DesiderateAPI();

  let preferences = props.preferences ;
  let toDelPreferences = props.toDelPreferences ;
  const setPreferences = props.updatePrefs ;

  async function checkMayBeSent() {
    const canSend = preferences.reduce((total, value) => {
      return total && value.turnKinds.length !== 0 ;
    }, true) ;

    if(canSend) {
      await saveDesiderate() ;
    } else {
      toast(t('Select at least a turn for each date'), {
        position: 'top-center',
        autoClose: 1500,
        style : {background : "red", color : "white"}
      })
    }
  }
  async function saveDesiderate() {
    let response
    try {
      response = await(desiderataApi.editDesiderate(preferences, toDelPreferences,id))
    } catch (err) {

      panic()
      return
    }
    let responseStatus  = response.status

    if (responseStatus === 202) {
      setPreferences(response, []) ;
      toast.success(t('Preferences added successfully'), {
        position: "top-center",
        autoClose: 5000,
        hideProgressBar: true,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
        theme: "colored",
      });
    } else if (responseStatus === 400) {
      toast.error(t('Error loading preferences'), {
        position: "top-center",
        autoClose: 5000,
        hideProgressBar: true,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
        theme: "colored",
      });
    }
  }

  function checkBoxValue(singlePref, label) {
    return singlePref.turnKinds.reduce((total, value) => {
      return total || value === label
    }, false) ;
  }

  function checkBoxChange(singlePref, label) {
    const lastValue = checkBoxValue(singlePref, label) ;

    if(lastValue) /*This means it is now being set to false*/{
      singlePref.turnKinds = singlePref.turnKinds.filter((value) => {return value !== label}) ;
    } else /*This means it is now set to true*/{
      singlePref.turnKinds = singlePref.turnKinds.concat([label]) ;
    }

    preferences = preferences.slice(0) ;
    setPreferences(preferences, toDelPreferences) ;
  }

  const sortedPrefs = preferences.sort((a,b) => {return a.data.getTime() - b.data.getTime()}) ;
  const processedPrefs = sortedPrefs.map(singlePref => {
    return (<div style={{paddingRight : 10, paddingLeft : 10, paddingTop : 5, paddingBottom : 5}}>
        <div style={{display: 'flex',  justifyContent:'space-between', alignItems:'center'}}>
          {new DateObject(singlePref.data).format("DD/MM/YYYY")}
          <Button onClick={() => {
            preferences = preferences.filter((value) => {return value !== singlePref})
            toDelPreferences = toDelPreferences.concat([singlePref]) ;
            setPreferences(preferences, toDelPreferences) ;
          }}>{t('Delete')}</Button>
        </div>
        <div style={{paddingRight : 40, paddingLeft : 40, display : "flex", alignItems : "center"}}>
          <div style={{paddingLeft: 30, paddingRight: 10, paddingTop: 5}}>
            <input type={"checkbox"} name={"morning"} checked={checkBoxValue(singlePref, "MORNING")} onChange={e => {
                     checkBoxChange(singlePref, "MORNING")
                     return !(e.target.checked)
                   }}/>
          </div>
          <div style={{paddingTop: 5}}>
            {t('Morning Shift')}
          </div>
        </div>
        <div style={{paddingRight: 40, paddingLeft: 40,  display : "flex", alignItems : "center"}}>
          <div style={{paddingLeft: 30, paddingRight: 10, paddingTop: 5}}>
            <input type={"checkbox"} name={"afternoon"}
                   checked={checkBoxValue(singlePref, "AFTERNOON")}
                   onChange={e => {
                     checkBoxChange(singlePref, "AFTERNOON")
                     return !(e.target.checked)
                   }}/>
          </div>
          <div style={{paddingTop: 5}}>
            {t('Afternoon Shift')}
          </div>
        </div>
        <div style={{paddingRight: 40, paddingLeft: 40, display : "flex", alignItems : "center"}}>
          <div style={{paddingLeft: 30, paddingRight: 10, paddingTop: 5}}>
            <input type={"checkbox"} name={"night"}
                   checked={checkBoxValue(singlePref, "NIGHT")} onChange={e => {
              checkBoxChange(singlePref, "NIGHT")
              return !(e.target.checked)
            }}/>
          </div>
          <div style={{paddingTop : 5}}>
            {t('Night Shift')}
          </div>
        </div>
      </div>
    )
  });

  return (
    <div style={{float: "none"}} >
      <div style={{minWidth: 300, height: 400, overflowY: "scroll"}}>
        {processedPrefs}
      </div>
      <div>
        <Button position="bottom" onClick={checkMayBeSent} >{t('Save')}</Button>
      </div>
    </div>
  )
}

export default function PreferencesDatePick(props) {

  const datePickerRef = useRef() ;
  let preferences = props.desiderate ;
  let toDelPreferences = props.toDelPrefs ;
  const setPreferences = props.setDesiderate ;

  let newProps = {} ;
  newProps.datePickerRef = datePickerRef ;
  newProps.preferences = preferences ;
  newProps.toDelPreferences = toDelPreferences ;
  newProps.updatePrefs = setPreferences ;


  function updateSelectedDates(date) {

    for (let i = 0 ; i < date.length ; i ++) {

      const toRestorePref = toDelPreferences.find((value) => {
        return value.data.toDateString() === date[i].toDate().toDateString()  ;
      }) ;

      const condition = preferences.reduce((total, value) => {
        return total && !(value.data.toDateString()  === date[i].toDate().toDateString() ) }, true) ;

      if(toRestorePref !== undefined) {

        toDelPreferences = toDelPreferences.filter((value) => {
          return value.data.toDateString()  !== date[i].toDate().toDateString()  ;
        }) ;

        preferences = preferences.concat([toRestorePref]) ;

      } else if (condition) {
        const preference = {
          data : date[i].toDate(),
          turnKinds : []
        }
        preferences = preferences.concat([preference]) ;
      }
    }

    preferences.forEach((value) => {
      const condition = date.reduce((total, value1) => {
        return total || (value.data.toDateString() === value1.toDate().toDateString()) }, false) ;

      if(!condition) {
        toDelPreferences = toDelPreferences.concat([value]) ;
        preferences = preferences.filter((value1) => {
          return value1 !== value
        }) ;
      }
    }) ;

    setPreferences(preferences, toDelPreferences) ;
  }

  return (

    <div style={{display : "flex", justifyContent : "center"}}>
      <div style={{float: "left", paddingTop : 120, paddingBottom : 120}}>
        <Calendar
          mapDays={({date}) => {
            let desiderataPresente = false
            for (let i = 0; i < props.desiderate.length; i++) {
              if (date.format("DD/M/YYYY") === props.desiderate[i].data) {
                desiderataPresente = true
                break
              }
            }
            if (desiderataPresente) return {
              disabled: true,
              style: {color: "#ccc"},
              onClick: () => toast.error(t('Preference already present'), {
                position: "top-center",
                autoClose: 5000,
                hideProgressBar: true,
                closeOnClick: true,
                pauseOnHover: true,
                draggable: true,
                progress: undefined,
                theme: "colored",
              })
            }
          }}
          className="teal"
          plugins={[
            <ToolBar
              position="bottom"
              names={{
                today: t('Today'),
                deselect: t('Deselect')
              }}
            />
          ]}
          multiple
          containerStyle={{width: "100%", height: "100%"}}
          style={{
            width: "100%",
            height: "100%",
            boxSizing: "border-box"
          }}
          months={months}
          weekDays={weeksName}
          currentDate={true}
          numberOfMonths={1}
          minDate={new Date()}
          //maxDate={} TO DO : massima data per lo scheduler
          onChange={updateSelectedDates}
          value={preferences.map((value) => {
            return value.data
          })}
          calendarPosition="top-right"
        />
      </div>
      <div style={{float: "left", paddingLeft : 10}}>
        <MDBCard>
          <DateSelectSlots props={newProps}/>
        </MDBCard>
      </div>

    </div>

  )
}
