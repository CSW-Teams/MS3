DO $$
BEGIN

    -- Cancellare lo schema 'public' nel database ms3_a
    PERFORM dblink_exec(
        'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
        'DROP SCHEMA IF EXISTS public CASCADE'
    );

    PERFORM dblink_exec(
        'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
        'CREATE SCHEMA public'
    );

    -- Cancellare lo schema 'public' nel database ms3_b
    PERFORM dblink_exec(
        'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
        'DROP SCHEMA IF EXISTS public CASCADE'
    );

    PERFORM dblink_exec(
        'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
        'CREATE SCHEMA public'
    );

    PERFORM dblink_exec(
            'host=localhost dbname=ms3_public user=sprintfloyd password=sprintfloyd',
            'DROP SCHEMA IF EXISTS public CASCADE'
        );

        PERFORM dblink_exec(
            'host=localhost dbname=ms3_public user=sprintfloyd password=sprintfloyd',
            'CREATE SCHEMA public'
        );

   -- Creazione della tabella ms3_system_users nel database central_db
   PERFORM dblink_exec(
      'host=localhost dbname=ms3_public user=sprintfloyd password=sprintfloyd',
      'CREATE TABLE IF NOT EXISTS ms3_system_users (
          id BIGSERIAL PRIMARY KEY,
          name VARCHAR(255) NOT NULL,
          lastname VARCHAR(255) NOT NULL,
          birthday DATE NOT NULL,
          tax_code VARCHAR(255) NOT NULL,
          email VARCHAR(255) UNIQUE NOT NULL,
          password VARCHAR(255) NOT NULL,
          tenant VARCHAR(255) NOT NULL
      )'
   );


   -- Creazione della tabella nel database tenant_a
   PERFORM dblink_exec(
      'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
      'CREATE TABLE IF NOT EXISTS doctors (
          id BIGSERIAL PRIMARY KEY,
          name VARCHAR(255) NOT NULL,
          lastname VARCHAR(255) NOT NULL,
          birthday DATE NOT NULL,
          tax_code VARCHAR(255) NOT NULL,
          email VARCHAR(255) UNIQUE NOT NULL,
          password VARCHAR(255) NOT NULL
      )'
   );

   -- Creazione della tabella nel database tenant_b
   PERFORM dblink_exec(
      'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
      'CREATE TABLE IF NOT EXISTS doctors (
          id BIGSERIAL PRIMARY KEY,
          name VARCHAR(255) NOT NULL,
          lastname VARCHAR(255) NOT NULL,
          birthday DATE NOT NULL,
          tax_code VARCHAR(255) NOT NULL,
          email VARCHAR(255) UNIQUE NOT NULL,
          password VARCHAR(255) NOT NULL
      )'
   );

   PERFORM dblink_exec(
         'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
         'CREATE TABLE IF NOT EXISTS holiday (
             id                   bigint       not null
                     primary key,
                 category             integer      not null,
                 custom               boolean      not null,
                 end_date_epoch_day   bigint       not null,
                 location             varchar(255),
                 name                 varchar(255) not null,
                 start_date_epoch_day bigint       not null
         )'
   );

   PERFORM dblink_exec(
            'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
            'CREATE TABLE IF NOT EXISTS recurrent_holiday (
                id          bigint       not null
                        primary key,
                    category    integer      not null,
                    end_day     integer      not null,
                    end_month   integer      not null,
                    location    varchar(255),
                    name        varchar(255) not null,
                    start_day   integer      not null,
                    start_month integer      not null
            )'
      );

      PERFORM dblink_exec(
           'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
           'CREATE TABLE IF NOT EXISTS holiday (
               id                   bigint       not null
                       primary key,
                   category             integer      not null,
                   custom               boolean      not null,
                   end_date_epoch_day   bigint       not null,
                   location             varchar(255),
                   name                 varchar(255) not null,
                   start_date_epoch_day bigint       not null
           )'
     );

     PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS recurrent_holiday (
                  id          bigint       not null
                          primary key,
                      category    integer      not null,
                      end_day     integer      not null,
                      end_month   integer      not null,
                      location    varchar(255),
                      name        varchar(255) not null,
                      start_day   integer      not null,
                      start_month integer      not null
              )'
        );

        PERFORM dblink_exec(
            'host=localhost dbname=ms3_public user=sprintfloyd password=sprintfloyd',
            'CREATE SEQUENCE IF NOT EXISTS hibernate_sequence'
        );
        PERFORM dblink_exec(
            'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
            'CREATE SEQUENCE IF NOT EXISTS hibernate_sequence'
        );
        PERFORM dblink_exec(
            'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
            'CREATE SEQUENCE IF NOT EXISTS hibernate_sequence'
        );

        PERFORM dblink_exec(
            'host=localhost dbname=ms3_public user=sprintfloyd password=sprintfloyd',
            'CREATE SEQUENCE IF NOT EXISTS constraint_id_seq increment by 50'
        );
        PERFORM dblink_exec(
            'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
            'CREATE SEQUENCE IF NOT EXISTS constraint_id_seq increment by 50'
        );
        PERFORM dblink_exec(
            'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
            'CREATE SEQUENCE IF NOT EXISTS constraint_id_seq increment by 50'
        );

        PERFORM dblink_exec(
              'host=localhost dbname=ms3_public user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS hibernate_sequences (
                  sequence_name varchar(255) not null
                          primary key,
                      next_val      bigint
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS hibernate_sequences (
                  sequence_name varchar(255) not null
                          primary key,
                      next_val      bigint
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS hibernate_sequences (
                  sequence_name varchar(255) not null
                          primary key,
                      next_val      bigint
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_public user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS ms3_system_user (
                  ms3_system_user_id bigint       not null
                          primary key,
                      birthday           date         not null,
                      email              varchar(255) not null,
                      lastname           varchar(255) not null,
                      name               varchar(255) not null,
                      password           varchar(255) not null,
                      tax_code           varchar(255) not null,
                      tenant             varchar(255) not null
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_public user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS user_system_actors(
                  user_ms3_system_user_id bigint not null,
                      system_actors           integer
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_public user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS systemuser_systemactors(
                  ms3_system_user_id bigint      not null,
                      role               varchar(50) not null,
                      primary key (ms3_system_user_id, role)
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS condition(
                  id   bigserial
                          primary key,
                      type varchar(255) not null
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS condition(
                  id   bigserial
                          primary key,
                      type varchar(255) not null
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS permanent_condition(
                  id bigint not null
                          primary key
                          constraint fkidbh8f1v99ueewy58lfajs3g3
                              references condition
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS permanent_condition(
                  id bigint not null
                          primary key
                          constraint fkidbh8f1v99ueewy58lfajs3g3
                              references condition
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS temporary_condition(
                  end_date   bigint not null,
                      start_date bigint not null,
                      id         bigint not null
                          primary key
                          constraint fkb303kbmim9pt133tyojbnay6m
                              references condition
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS temporary_condition(
                  end_date   bigint not null,
                      start_date bigint not null,
                      id         bigint not null
                          primary key
                          constraint fkb303kbmim9pt133tyojbnay6m
                              references condition
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS specialization(
                  specialization_id bigserial
                          primary key,
                      type              varchar(255) not null
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS specialization(
                  specialization_id bigserial
                          primary key,
                      type              varchar(255) not null
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS task(
                  task_id   bigint not null
                          primary key,
                      task_type integer
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS task(
                  task_id   bigint not null
                          primary key,
                      task_type integer
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS task_with_assignmentdto(
                  task_id     bigint  not null
                          primary key,
                      is_assigned boolean not null,
                      task_type   integer
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS task_with_assignmentdto(
                  task_id     bigint  not null
                          primary key,
                      is_assigned boolean not null,
                      task_type   integer
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS medical_service(
                  medical_service_id bigserial
                          primary key,
                      label              varchar(255) not null
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS medical_service(
                  medical_service_id bigserial
                          primary key,
                      label              varchar(255) not null
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS medical_service_tasks(
                  medical_service_medical_service_id bigint not null
                          constraint fkmb680028kpvielpqvutiqkmw9
                              references medical_service,
                      tasks_task_id                      bigint not null
                          constraint fkavku6paea867say2pcg2diihm
                              references task
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS medical_service_tasks(
                  medical_service_medical_service_id bigint not null
                          constraint fkmb680028kpvielpqvutiqkmw9
                              references medical_service,
                      tasks_task_id                      bigint not null
                          constraint fkavku6paea867say2pcg2diihm
                              references task
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS doctor(
                  ms3_tenant_user_id bigint       not null
                          primary key,
                      birthday           date         not null,
                      email              varchar(255) not null,
                      lastname           varchar(255) not null,
                      name               varchar(255) not null,
                      password           varchar(255) not null,
                      tax_code           varchar(255) not null,
                      seniority          integer      not null
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS doctor(
                  ms3_tenant_user_id bigint       not null
                          primary key,
                      birthday           date         not null,
                      email              varchar(255) not null,
                      lastname           varchar(255) not null,
                      name               varchar(255) not null,
                      password           varchar(255) not null,
                      tax_code           varchar(255) not null,
                      seniority          integer      not null
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS doctor_holidays(
                  doctor_holidays_id        bigint not null
                          primary key,
                      holiday_map               oid    not null,
                      doctor_ms3_tenant_user_id bigint not null
                          constraint fkfvgbw7dtyh2udi5gt75bbkmtl
                              references doctor
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS doctor_holidays(
                  doctor_holidays_id        bigint not null
                          primary key,
                      holiday_map               oid    not null,
                      doctor_ms3_tenant_user_id bigint not null
                          constraint fkfvgbw7dtyh2udi5gt75bbkmtl
                              references doctor
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS ms3_tenant_user(
                  ms3_tenant_user_id bigint       not null
                          primary key,
                      birthday           date         not null,
                      email              varchar(255) not null,
                      lastname           varchar(255) not null,
                      name               varchar(255) not null,
                      password           varchar(255) not null,
                      tax_code           varchar(255) not null
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS ms3_tenant_user(
                  ms3_tenant_user_id bigint       not null
                          primary key,
                      birthday           date         not null,
                      email              varchar(255) not null,
                      lastname           varchar(255) not null,
                      name               varchar(255) not null,
                      password           varchar(255) not null,
                      tax_code           varchar(255) not null
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS user_tenant_actors(
                  user_ms3_tenant_user_id bigint not null,
                      system_actors           integer
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS user_tenant_actors(
                  user_ms3_tenant_user_id bigint not null,
                      system_actors           integer
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS tenantuser_systemactors(
                  ms3_tenant_user_id bigint      not null,
                      role               varchar(50) not null,
                      primary key (ms3_tenant_user_id, role)
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS tenantuser_systemactors(
                  ms3_tenant_user_id bigint      not null,
                                        role               varchar(50) not null,
                                        primary key (ms3_tenant_user_id, role)
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS shift(
                  shift_id                           bigint  not null
                          primary key,
                      duration                           bigint  not null,
                      start_time                         time    not null,
                      time_slot                          integer not null,
                      medical_service_medical_service_id bigint
                          constraint fk4vf2jsq4n82b7upj2l6ct8lpt
                              references medical_service
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS shift(
                  shift_id                           bigint  not null
                          primary key,
                      duration                           bigint  not null,
                      start_time                         time    not null,
                      time_slot                          integer not null,
                      medical_service_medical_service_id bigint
                          constraint fk4vf2jsq4n82b7upj2l6ct8lpt
                              references medical_service
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS ms3_constraint(
                  dtype                   varchar(31)  not null,
                      constraint_id           bigint       not null
                          primary key,
                      description             varchar(255) not null,
                      violable                boolean      not null,
                      horizon                 integer,
                      t_unit                  varchar(255),
                      time_slot               varchar(255),
                      max_consecutive_minutes bigint,
                      period_duration         integer,
                      period_max_time         bigint,
                      constrained_category_id bigint
                          constraint fk56q3fx7puxy8fecicqeugo8cv
                              references condition
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS ms3_constraint(
                  dtype                   varchar(31)  not null,
                      constraint_id           bigint       not null
                          primary key,
                      description             varchar(255) not null,
                      violable                boolean      not null,
                      horizon                 integer,
                      t_unit                  varchar(255),
                      time_slot               varchar(255),
                      max_consecutive_minutes bigint,
                      period_duration         integer,
                      period_max_time         bigint,
                      constrained_category_id bigint
                          constraint fk56q3fx7puxy8fecicqeugo8cv
                              references condition
              )'
        );


        PERFORM dblink_exec(
              'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS shift_additional_constraints(
                  shift_shift_id                       bigint not null
                          constraint fkmumt09206y7o4jk16qur6ebru
                              references shift,
                      additional_constraints_constraint_id bigint not null
                          constraint fk25gyof9uyb6hiohf3ex982a6i
                              references ms3_constraint
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS shift_additional_constraints(
                  shift_shift_id                       bigint not null
                          constraint fkmumt09206y7o4jk16qur6ebru
                              references shift,
                      additional_constraints_constraint_id bigint not null
                          constraint fk25gyof9uyb6hiohf3ex982a6i
                              references ms3_constraint
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS shift_days_of_week(
                  shift_shift_id bigint not null
                          constraint fk9jk9s9ls1iwl5qu582p6nu9ug
                              references shift,
                      days_of_week   integer
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS shift_days_of_week(
                  shift_shift_id bigint not null
                          constraint fk9jk9s9ls1iwl5qu582p6nu9ug
                              references shift,
                      days_of_week   integer
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS quantity_shift_seniority(
                  id            bigint not null
                          primary key,
                      seniority_map oid,
                      task_task_id  bigint
                          constraint fkqkkefb28jj74gk9as5lujuoht
                              references task
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS quantity_shift_seniority(
                  id            bigint not null
                          primary key,
                      seniority_map oid,
                      task_task_id  bigint
                          constraint fkqkkefb28jj74gk9as5lujuoht
                              references task
              )'
        );

        PERFORM dblink_exec(
              'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS shift_quantity_shift_seniority(
                  shift_shift_id              bigint not null
                          constraint fk147xktaiif9ueng5l2oklby27
                              references shift,
                      quantity_shift_seniority_id bigint not null
                          constraint uk_657e2opyl1mwcjii8a8qdmn9y
                              unique
                          constraint fkoamnh1j4jcb2vn4r8ftn2ondh
                              references quantity_shift_seniority
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS shift_quantity_shift_seniority(
                  shift_shift_id              bigint not null
                          constraint fk147xktaiif9ueng5l2oklby27
                              references shift,
                      quantity_shift_seniority_id bigint not null
                          constraint uk_657e2opyl1mwcjii8a8qdmn9y
                              unique
                          constraint fkoamnh1j4jcb2vn4r8ftn2ondh
                              references quantity_shift_seniority
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS doctor_permanent_conditions(
                  doctor_ms3_tenant_user_id bigint not null
                          constraint fk1qa08xedirepqn7v573whdh2d
                              references doctor,
                      permanent_conditions_id   bigint not null
                          constraint fkbewktrk61ubphcu7cnegu2y3b
                              references permanent_condition
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS doctor_permanent_conditions(
                  doctor_ms3_tenant_user_id bigint not null
                          constraint fk1qa08xedirepqn7v573whdh2d
                              references doctor,
                      permanent_conditions_id   bigint not null
                          constraint fkbewktrk61ubphcu7cnegu2y3b
                              references permanent_condition
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS preference(
                  id   bigint not null
                          primary key,
                      date date
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS preference(
                  id   bigint not null
                          primary key,
                      date date
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS preference_doctors(
                  preference_id              bigint not null
                          constraint fkhrgatrsccsp0kry9kb9ft8sh0
                              references preference,
                      doctors_ms3_tenant_user_id bigint not null
                          constraint fkyqbccg77ndmev6jov5awru6t
                              references doctor
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS preference_doctors(
                  preference_id              bigint not null
                          constraint fkhrgatrsccsp0kry9kb9ft8sh0
                              references preference,
                      doctors_ms3_tenant_user_id bigint not null
                          constraint fkyqbccg77ndmev6jov5awru6t
                              references doctor
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS preference_time_slots(
                  preference_id bigint not null
                          constraint fk41q4kloomdt3wyixirwn3sudt
                              references preference,
                      time_slots    integer
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS preference_time_slots(
                  preference_id bigint not null
                          constraint fk41q4kloomdt3wyixirwn3sudt
                              references preference,
                      time_slots    integer
              )'
        );

        PERFORM dblink_exec(
              'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS doctor_preference_list(
                  doctor_ms3_tenant_user_id bigint not null
                          constraint fkrftrnnnu2e47wgdk4dkcoqgia
                              references doctor,
                      preference_list_id        bigint not null
                          constraint fkigigf535imqq5mubfmfjonalu
                              references preference
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS doctor_preference_list(
                  doctor_ms3_tenant_user_id bigint not null
                          constraint fkrftrnnnu2e47wgdk4dkcoqgia
                              references doctor,
                      preference_list_id        bigint not null
                          constraint fkigigf535imqq5mubfmfjonalu
                              references preference
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS doctor_specializations(
                  doctor_ms3_tenant_user_id         bigint not null
                          constraint fkrp1pvfw6amwvi3xdd176lc2tk
                              references doctor,
                      specializations_specialization_id bigint not null
                          constraint fk9gld6lk88tvf6o3dirub3i90
                              references specialization
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS doctor_specializations(
                  doctor_ms3_tenant_user_id         bigint not null
                          constraint fkrp1pvfw6amwvi3xdd176lc2tk
                              references doctor,
                      specializations_specialization_id bigint not null
                          constraint fk9gld6lk88tvf6o3dirub3i90
                              references specialization
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS doctor_temporary_conditions(
                  doctor_ms3_tenant_user_id bigint not null
                          constraint fk2r8hut52kv1k65vjxn4732nyl
                              references doctor,
                      temporary_conditions_id   bigint not null
                          constraint fkhvhnnfsoeadi43aihyft35wj1
                              references temporary_condition
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS doctor_temporary_conditions(
                  doctor_ms3_tenant_user_id bigint not null
                          constraint fk2r8hut52kv1k65vjxn4732nyl
                              references doctor,
                      temporary_conditions_id   bigint not null
                          constraint fkhvhnnfsoeadi43aihyft35wj1
                              references temporary_condition
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS schedule(
                  schedule_id   bigint not null
                          primary key,
                      cause_illegal bytea,
                      end_date      bigint not null,
                      start_date    bigint not null
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS schedule(
                  schedule_id   bigint not null
                          primary key,
                      cause_illegal bytea,
                      end_date      bigint not null,
                      start_date    bigint not null
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS concrete_shift(
                  concrete_shift_id bigint not null
                          primary key,
                      date              bigint not null,
                      shift_shift_id    bigint not null
                          constraint fkjbedgfkknt1v42vexkh8h56d8
                              references shift
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS concrete_shift(
                  concrete_shift_id bigint not null
                          primary key,
                      date              bigint not null,
                      shift_shift_id    bigint not null
                          constraint fkjbedgfkknt1v42vexkh8h56d8
                              references shift
              )'
        );

        PERFORM dblink_exec(
              'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS schedule_concrete_shifts(
                  schedule_schedule_id              bigint not null
                          constraint fk1nccylsrcvlpbcfpij716ejrk
                              references schedule,
                      concrete_shifts_concrete_shift_id bigint not null
                          constraint uk_cw9jkumdwnmyt684g8dxtbi13
                              unique
                          constraint fkaily47udtim6bm3u1pt16qi6j
                              references concrete_shift
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS schedule_concrete_shifts(
                  schedule_schedule_id              bigint not null
                          constraint fk1nccylsrcvlpbcfpij716ejrk
                              references schedule,
                      concrete_shifts_concrete_shift_id bigint not null
                          constraint uk_cw9jkumdwnmyt684g8dxtbi13
                              unique
                          constraint fkaily47udtim6bm3u1pt16qi6j
                              references concrete_shift
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS schedule_violated_constraints(
                  schedule_schedule_id               bigint not null
                          constraint fko3aerkdgl1ucibcal8iwiiq0u
                              references schedule,
                      violated_constraints_constraint_id bigint not null
                          constraint fkcrmao7o7l611d63pw7jcgp8dl
                              references ms3_constraint
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS schedule_violated_constraints(
                  schedule_schedule_id               bigint not null
                          constraint fko3aerkdgl1ucibcal8iwiiq0u
                              references schedule,
                      violated_constraints_constraint_id bigint not null
                          constraint fkcrmao7o7l611d63pw7jcgp8dl
                              references ms3_constraint
              )'
        );
        PERFORM dblink_exec(
                'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
                'CREATE TABLE IF NOT EXISTS user_schedule_state(
                    id                        bigint  not null
                            primary key,
                        uffa_cumulativo           integer not null,
                        uffa_parziale             integer not null,
                        schedule_schedule_id      bigint
                            constraint fkrw089fxqcwklfx8g9tetvt4jd
                                references schedule,
                        utente_ms3_tenant_user_id bigint
                )'
          );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS user_schedule_state(
                  id                        bigint  not null
                          primary key,
                      uffa_cumulativo           integer not null,
                      uffa_parziale             integer not null,
                      schedule_schedule_id      bigint
                          constraint fkrw089fxqcwklfx8g9tetvt4jd
                              references schedule,
                      utente_ms3_tenant_user_id bigint
              )'
        );

        PERFORM dblink_exec(
              'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS doctor_uffa_priority(
                  id                          bigint  not null
                          primary key,
                      general_priority            integer not null,
                      long_shift_priority         integer not null,
                      night_priority              integer not null,
                      partial_general_priority    integer not null,
                      partial_long_shift_priority integer not null,
                      partial_night_priority      integer not null,
                      doctor_ms3_tenant_user_id   bigint  not null
                          constraint fk4nankuxa09wc9qh4grgaig502
                              references doctor,
                      schedule_schedule_id        bigint
                          constraint fkov3bg2k966dirumhcl7oji82r
                              references schedule
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS doctor_uffa_priority(
                  id                          bigint  not null
                          primary key,
                      general_priority            integer not null,
                      long_shift_priority         integer not null,
                      night_priority              integer not null,
                      partial_general_priority    integer not null,
                      partial_long_shift_priority integer not null,
                      partial_night_priority      integer not null,
                      doctor_ms3_tenant_user_id   bigint  not null
                          constraint fk4nankuxa09wc9qh4grgaig502
                              references doctor,
                      schedule_schedule_id        bigint
                          constraint fkov3bg2k966dirumhcl7oji82r
                              references schedule
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS doctor_uffa_priority_snapshot(
                  id                        bigint  not null
                          primary key,
                      general_priority          integer not null,
                      long_shift_priority       integer not null,
                      night_priority            integer not null,
                      doctor_ms3_tenant_user_id bigint  not null
                          constraint fkbcsgo0mtncu0vwgxvo5s731eh
                              references doctor,
                      schedule_schedule_id      bigint
                          constraint fkex1084c0m80pdp0bsm16lk35b
                              references schedule
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS doctor_uffa_priority_snapshot(
                  id                        bigint  not null
                          primary key,
                      general_priority          integer not null,
                      long_shift_priority       integer not null,
                      night_priority            integer not null,
                      doctor_ms3_tenant_user_id bigint  not null
                          constraint fkbcsgo0mtncu0vwgxvo5s731eh
                              references doctor,
                      schedule_schedule_id      bigint
                          constraint fkex1084c0m80pdp0bsm16lk35b
                              references schedule
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS config(
                  name       varchar(255) not null
                          primary key,
                      first_boot boolean      not null
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS config(
                  name       varchar(255) not null
                          primary key,
                      first_boot boolean      not null
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS config_vinc_max_per_cons(
                  max_periodo_id           bigserial
                          primary key,
                      max_consecutive_minutes  integer not null,
                      constrained_condition_id bigint  not null
                          constraint fk3tnkfvlqsgpkjlyn5h40jtad9
                              references condition
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS config_vinc_max_per_cons(
                  max_periodo_id           bigserial
                          primary key,
                      max_consecutive_minutes  integer not null,
                      constrained_condition_id bigint  not null
                          constraint fk3tnkfvlqsgpkjlyn5h40jtad9
                              references condition
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS config_vincoli(
                  config_vincoli_id                 bigserial
                          primary key,
                      horizon_night_shift               integer not null,
                      max_consecutive_time_for_everyone integer not null,
                      period_days_no                    integer not null,
                      period_max_time                   integer not null
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS config_vincoli(
                  config_vincoli_id                 bigserial
                          primary key,
                      horizon_night_shift               integer not null,
                      max_consecutive_time_for_everyone integer not null,
                      period_days_no                    integer not null,
                      period_max_time                   integer not null
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS config_vincoli_config_vinc_max_per_cons_per_categoria(
                  config_vincoli_config_vincoli_id                      bigint not null
                          constraint fkcd06cj6l9aw4qvhkpr4f9kvm2
                              references config_vincoli,
                      config_vinc_max_per_cons_per_categoria_max_periodo_id bigint not null
                          constraint uk_7rtyc4098bip4hsr621xsbmuq
                              unique
                          constraint fkj89no7ryhto0pi3831yp1hdgs
                              references config_vinc_max_per_cons
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS config_vincoli_config_vinc_max_per_cons_per_categoria(
                  config_vincoli_config_vincoli_id                      bigint not null
                          constraint fkcd06cj6l9aw4qvhkpr4f9kvm2
                              references config_vincoli,
                      config_vinc_max_per_cons_per_categoria_max_periodo_id bigint not null
                          constraint uk_7rtyc4098bip4hsr621xsbmuq
                              unique
                          constraint fkj89no7ryhto0pi3831yp1hdgs
                              references config_vinc_max_per_cons
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS constraint_turni_contigui_forbidden_time_slots(
                  constraint_turni_contigui_constraint_id bigint not null
                          constraint fkjrdi3549kx7mwtr47m0fkk098
                              references ms3_constraint,
                      forbidden_time_slots                    varchar(255)
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS constraint_turni_contigui_forbidden_time_slots(
                  constraint_turni_contigui_constraint_id bigint not null
                          constraint fkjrdi3549kx7mwtr47m0fkk098
                              references ms3_constraint,
                      forbidden_time_slots                    varchar(255)
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS violated_constraint_log_entry(
                  id        bigint not null
                          primary key,
                      violation bytea
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS violated_constraint_log_entry(
                  id        bigint not null
                          primary key,
                      violation bytea
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS scocciatura(
                  dtype            varchar(31) not null,
                      id               bigint      not null
                          primary key,
                      peso             integer,
                      time_slot        integer,
                      giorno_settimana integer,
                      vacanza_id       bigint
                          constraint fklmak2608yw4bmoy9rb9vp3f28
                              references holiday
                              on delete cascade
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS scocciatura(
                  dtype            varchar(31) not null,
                      id               bigint      not null
                          primary key,
                      peso             integer,
                      time_slot        integer,
                      giorno_settimana integer,
                      vacanza_id       bigint
                          constraint fklmak2608yw4bmoy9rb9vp3f28
                              references holiday
                              on delete cascade
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS notification(
                  notification_id         bigint       not null
                          primary key,
                      message                 varchar(255) not null,
                      status                  boolean      not null,
                      user_ms3_tenant_user_id bigint       not null
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS notification(
                  notification_id         bigint       not null
                          primary key,
                      message                 varchar(255) not null,
                      status                  boolean      not null,
                      user_ms3_tenant_user_id bigint       not null
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS request(
                  id                          bigint not null
                          primary key,
                      status                      integer,
                      receiver_ms3_tenant_user_id bigint not null,
                      sender_ms3_tenant_user_id   bigint not null,
                      turn_concrete_shift_id      bigint not null
                          constraint fkt6xetpaexs3090h0gpfhk2joh
                              references concrete_shift
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS request(
                  id                          bigint not null
                          primary key,
                      status                      integer,
                      receiver_ms3_tenant_user_id bigint not null,
                      sender_ms3_tenant_user_id   bigint not null,
                      turn_concrete_shift_id      bigint not null
                          constraint fkt6xetpaexs3090h0gpfhk2joh
                              references concrete_shift
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_a user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS request_removal_from_concrete_shift(
                  request_removal_from_concrete_shift_id bigint       not null
                          primary key,
                      file                                   oid,
                      is_accepted                            boolean      not null,
                      is_reviewed                            boolean      not null,
                      reason                                 varchar(255) not null,
                      concrete_shift_id                      bigint       not null
                          constraint fklhfoqtrp3m12wpqu9hikc307h
                              references concrete_shift,
                      requesting_doctor_ms3_tenant_user_id   bigint       not null
                          constraint fk2hai5b9f0lhlxod5va89o7kui
                              references doctor,
                      substitute_doctor_ms3_tenant_user_id   bigint
                          constraint fksaco8qurl42be85an4go2drro
                              references doctor
              )'
        );
        PERFORM dblink_exec(
              'host=localhost dbname=ms3_b user=sprintfloyd password=sprintfloyd',
              'CREATE TABLE IF NOT EXISTS request_removal_from_concrete_shift(
                  request_removal_from_concrete_shift_id bigint       not null
                          primary key,
                      file                                   oid,
                      is_accepted                            boolean      not null,
                      is_reviewed                            boolean      not null,
                      reason                                 varchar(255) not null,
                      concrete_shift_id                      bigint       not null
                          constraint fklhfoqtrp3m12wpqu9hikc307h
                              references concrete_shift,
                      requesting_doctor_ms3_tenant_user_id   bigint       not null
                          constraint fk2hai5b9f0lhlxod5va89o7kui
                              references doctor,
                      substitute_doctor_ms3_tenant_user_id   bigint
                          constraint fksaco8qurl42be85an4go2drro
                              references doctor
              )'
        );






END $$;