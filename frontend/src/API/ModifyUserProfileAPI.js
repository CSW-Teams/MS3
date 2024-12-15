export class ModifyUserProfileAPI {
  constructor() {}

  async setUpdatedProfileInfos(conf){
    const requestOptions = {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(conf)
    };

    return await fetch('/api/users/user-profile/update-profile-info/',requestOptions);
  }
}
