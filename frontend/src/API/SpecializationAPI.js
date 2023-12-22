export class SpecializationsAPI{
    async getAllPossibleSpecializations() {

        const response = await fetch('/api/specializations/');
        const body = await response.json();

        const specializations = [];

        for (let i = 0; i < body.length; i++) {
            specializations[i] = body[i].nome;
        }

        return specializations;
    }

    async getSingleDoctorSpecializations(idUtente) {
        const response = await fetch('/api/specializations/doctor_id='+idUtente);
        const body = await response.json();
        const specializationList = [];

        for (let i = 0; i < body.length; i++) {
            let specialization = {};
            specialization.categoriaUtenteId = body[i].id
            specialization.categoria = body[i].categoria.nome
            specialization.inizio = body[i].inizioValidita
            specialization.fine = body[i].fineValidita
            specializationList[i]=specialization
        }

        return specializationList;
    }
}
