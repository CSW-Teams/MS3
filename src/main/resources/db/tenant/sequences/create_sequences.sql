create sequence condition_id_seq;
alter sequence condition_id_seq owner to sprintfloyd;
alter sequence condition_id_seq owned by condition.id;

create sequence config_vinc_max_per_cons_max_periodo_id_seq;
alter sequence config_vinc_max_per_cons_max_periodo_id_seq owner to sprintfloyd;
alter sequence config_vinc_max_per_cons_max_periodo_id_seq owned by config_vinc_max_per_cons.max_periodo_id;

create sequence config_vincoli_config_vincoli_id_seq;
alter sequence config_vincoli_config_vincoli_id_seq owner to sprintfloyd;
alter sequence config_vincoli_config_vincoli_id_seq owned by config_vincoli.config_vincoli_id;

create sequence constraint_id_seq
    increment by 50;
alter sequence constraint_id_seq owner to sprintfloyd;

create sequence giustificazione_forzatura_vincoli_id_seq;
alter sequence giustificazione_forzatura_vincoli_id_seq owner to sprintfloyd;
alter sequence giustificazione_forzatura_vincoli_id_seq owned by giustificazione_forzatura_vincoli.id;

create sequence medical_service_medical_service_id_seq;
alter sequence medical_service_medical_service_id_seq owner to sprintfloyd;
alter sequence medical_service_medical_service_id_seq owned by medical_service.medical_service_id;

create sequence specialization_specialization_id_seq;
alter sequence specialization_specialization_id_seq owner to sprintfloyd;
alter sequence specialization_specialization_id_seq owned by specialization.specialization_id;

create sequence waiver_waiver_id_seq;
alter sequence waiver_waiver_id_seq owner to sprintfloyd;
alter sequence waiver_waiver_id_seq owned by waiver.waiver_id;