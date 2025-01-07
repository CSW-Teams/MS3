import {styled} from "@mui/material/styles";
import {getAppointmentColor, getResourceColor} from "../../multitenancyapp/utils/utils";
import {Appointments} from "@devexpress/dx-react-scheduler-material-ui";
import {lightBlue, red} from "@mui/material/colors";
import {blue, teal} from "@material-ui/core/colors";
const PREFIX_TOOLTIP = 'Content';


// Usare gli stili di queste classi per uniformare le viste

export const tooltip_classes = {
  content: `${PREFIX_TOOLTIP}-content`,
  text: `${PREFIX_TOOLTIP}-text`,
  title: `${PREFIX_TOOLTIP}-title`,
  icon: `${PREFIX_TOOLTIP}-icon`,
  lens: `${PREFIX_TOOLTIP}-lens`,
  lensMini: `${PREFIX_TOOLTIP}-lensMini`,
  textCenter: `${PREFIX_TOOLTIP}-textCenter`,
  dateAndTitle: `${PREFIX_TOOLTIP}-dateAndTitle`,
  titleContainer: `${PREFIX_TOOLTIP}-titleContainer`,
  contentContainer: `${PREFIX_TOOLTIP}-contentContainer`,
  resourceContainer: `${PREFIX_TOOLTIP}-resourceContainer`,
  recurringIcon: `${PREFIX_TOOLTIP}-recurringIcon`,
  relativeContainer: `${PREFIX_TOOLTIP}-relativeContainer`,
};


const PREFIX = 'Demo';


export const classes = {
  flexibleSpace: `${PREFIX}-flexibleSpace`,
  textField: `${PREFIX}-textField`,
  locationSelector: `${PREFIX}-locationSelector`,
  button: `${PREFIX}-button`,
  selectedButton: `${PREFIX}-selectedButton`,
  longButtonText: `${PREFIX}-longButtonText`,
  shortButtonText: `${PREFIX}-shortButtonText`,
  title: `${PREFIX}-title`,
  textContainer: `${PREFIX}-textContainer`,
  time: `${PREFIX}-time`,
  text: `${PREFIX}-text`,
  container: `${PREFIX}-container`,
  weekendCell: `${PREFIX}-weekendCell`,
  weekEnd: `${PREFIX}-weekEnd`, type: "--type",
  content: `${PREFIX}-content`,
  Holiday: `${PREFIX}-content`,
  Normal: `${PREFIX}-content`
};

//Stile di Div
export const StyledDiv = styled('div')(({
                                   theme: { spacing, palette, typography }, resources,
                                 }) => ({
  [`&.${tooltip_classes.content}`]: {
    padding: spacing(1.5, 1),
    paddingTop: spacing(1),
    backgroundColor: palette.background.paper,
    boxSizing: 'border-box',
    ...typography.body2,
  },
  [`& .${tooltip_classes.text}`]: {
    display: 'inline-block',
  },
  [`& .${tooltip_classes.title}`]: {
    ...typography.h6,
    color: palette.text.secondary,
    fontWeight: typography.fontWeightBold,
    overflow: 'hidden',
    textOverflow: 'ellipsis',
  },
  [`& .${tooltip_classes.icon}`]: {
    verticalAlign: 'middle',
    color: palette.action.active,
  },
  [`& .${tooltip_classes.lens}`]: {
    color: getAppointmentColor(300, getResourceColor(resources), palette.primary),
    width: spacing(4.5),
    height: spacing(4.5),
    verticalAlign: 'super',
    position: 'absolute',
    left: '50%',
    transform: 'translate(-50%,0)',
  },
  [`& .${tooltip_classes.lensMini}`]: {
    width: spacing(2.5),
    height: spacing(2.5),
  },
  [`& .${tooltip_classes.textCenter}`]: {
    textAlign: 'center',
    height: spacing(2.5),
  },
  [`& .${tooltip_classes.dateAndTitle}`]: {
    lineHeight: 1.4,
  },
  [`& .${tooltip_classes.titleContainer}`]: {
    paddingBottom: spacing(2),
  },
  [`& .${tooltip_classes.contentContainer}`]: {
    paddingBottom: spacing(1.5),
  },
  [`& .${tooltip_classes.resourceContainer}`]: {
    paddingBottom: spacing(0.25),
  },
  [`& .${tooltip_classes.recurringIcon}`]: {
    position: 'absolute',
    paddingTop: spacing(0.875),
    left: '50%',
    transform: 'translate(-50%,0)',
    color: palette.background.paper,
    width: spacing(2.625),
    height: spacing(2.625),
  },
  [`& .${tooltip_classes.relativeContainer}`]: {
    position: 'relative',
    width: '100%',
    height: '100%',
  },
}));


// Stile di AppointmentContent
export const StyledAppointmentsAppointmentContent = styled(Appointments.AppointmentContent)(() => ({
  [`& .${classes.title}`]: {
    fontWeight: 'bold',
    overflow: 'hidden',
    textOverflow: 'ellipsis',
    whiteSpace: 'nowrap',
  },
  [`& .${classes.type}`]: {
    overflow: 'hidden',
    textOverflow: 'ellipsis',
    whiteSpace: 'nowrap',
    labelTextColor: lightBlue,
  },
  [`& .${classes.content}`]: {
    fontSize: 12,
    fontWeight: 500,
    opacity: 0.7,
  },
  [`& .${classes.textContainer}`]: {
    lineHeight: 1,
    whiteSpace: 'pre-wrap',
    overflow: 'hidden',
    textOverflow: 'ellipsis',
    width: '100%',
  },
  [`& .${classes.time}`]: {
    display: 'inline-block',
    overflow: 'hidden',
    textOverflow: 'ellipsis',
  },
  [`& .${classes.text}`]: {
    overflow: 'hidden',
    textOverflow: 'ellipsis',
    whiteSpace: 'nowrap',
  },
  [`& .${classes.container}`]: {
    width: '100%',
  },
  [`&.${classes.Holiday}`]: {
    borderLeft: `4px solid ${red[500]}`,

  },
  [`&.${classes.Normal}`]: {
    borderLeft: `4px solid ${teal[500]}`,
  },
}));


