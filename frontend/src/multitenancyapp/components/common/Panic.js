import ReactModal from "react-modal";
import {useReducer} from "react";
import {MDBCard, MDBCardBody} from "mdb-react-ui-kit";
import { t } from "i18next";

export let panic = () => {}

export default function PanicTableau() {

  const [open, dispatch] = useReducer(() => { return true }, false, () => {})

  panic = dispatch

  return (
    <>
      <ReactModal isOpen={open} style={{
        overlay : {
          display : "flex",
          width : "200%", height : "200%", overflowY : "hidden",
          position : "absolute", top : 0, left : 0, zIndex : 10000,
          alignItems : "center", justifyContent : "center", background : "white"
        },
        content : {
          display : "flex",
          width : "200%", height : "200%", overflowY : "hidden",
          position : "absolute", top : 0, left : 0, zIndex : 10000,
          alignItems : "center", justifyContent : "center", background : "white"
        }
      }}>
        <div style={{position : "absolute", top : "10%", left : "5%", zIndex : 10000, fontSize : 40}}>
          {t('Connection Error, please try again later')}
        </div>
      </ReactModal>
    </>
  )
}
