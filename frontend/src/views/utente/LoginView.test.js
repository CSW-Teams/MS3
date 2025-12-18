import LoginView from "./LoginView";

jest.mock("i18next", () => ({
  t: (text) => text
}));

jest.mock("react-toastify", () => ({
  toast: {
    warn: jest.fn(),
    error: jest.fn(),
    info: jest.fn()
  }
}));

describe("LoginView two-factor flow", () => {
  const history = {push: jest.fn()};
  let component;

  beforeEach(() => {
    localStorage.clear();
    component = new LoginView({history});
  });

  test("does not persist JWT when two-factor challenge is presented", () => {
    component.handleTwoFactorChallenge({challenge: "abc", message: "Code required"}, 403);

    expect(localStorage.getItem("jwt")).toBeNull();
    expect(component.state.twoFactorDialogOpen).toBe(true);
    expect(component.state.twoFactorChallenge).toBe("abc");
  });

  test("stores lockout information when provided", () => {
    component.handleTwoFactorChallenge({message: "locked", retryAfterSeconds: 55}, 429);

    expect(component.state.lockoutInfo).toEqual({message: "locked", retryAfterSeconds: 55});
  });

  test("persists JWT only after completing login", async () => {
    component.handleDialogClose = jest.fn();

    await component.handleCompleteLogin({
      name: "Test",
      lastname: "User",
      tenant: "tenantA",
      jwt: "jwt-token",
      systemActors: ["Doctor"]
    });

    expect(localStorage.getItem("jwt")).toBe("jwt-token");
    expect(component.handleDialogClose).toHaveBeenCalledWith("Doctor");
  });
});
