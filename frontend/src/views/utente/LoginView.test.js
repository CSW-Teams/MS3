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
    // Given login requires second factor, when challenge response arrives, then JWT must not be persisted yet.
    // Regression guard: prevents 2FA bypass where partial login stores an authenticated token.
    component.handleTwoFactorChallenge({challenge: "abc", message: "Code required"}, 403);

    expect(localStorage.getItem("jwt")).toBeNull();
    expect(component.state.twoFactorDialogOpen).toBe(true);
    expect(component.state.twoFactorChallenge).toBe("abc");
  });

  test("stores lockout information when provided", () => {
    // Given backend lockout metadata, when challenge handler runs, then UI should preserve retry information for feedback.
    component.handleTwoFactorChallenge({message: "locked", retryAfterSeconds: 55}, 429);

    expect(component.state.lockoutInfo).toEqual({message: "locked", retryAfterSeconds: 55});
  });

  test("persists JWT only after completing login", async () => {
    // Given successful completion of the 2FA/login flow, when final payload is handled, then JWT can be safely stored.
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
