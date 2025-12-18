import TwoFactorEnrollmentView from "./TwoFactorEnrollmentView";
import {TwoFactorAPI} from "../../API/TwoFactorAPI";

jest.mock("i18next", () => ({
  t: (text) => text
}));

jest.mock("react-toastify", () => ({
  toast: {
    warn: jest.fn(),
    error: jest.fn(),
    info: jest.fn(),
    success: jest.fn()
  }
}));

jest.mock("../../API/TwoFactorAPI");

const makeResponse = (body, ok = true, status = 200) => ({
  ok,
  status,
  clone: () => ({
    json: async () => body
  })
});

describe("TwoFactorEnrollmentView", () => {
  beforeEach(() => {
    TwoFactorAPI.mockClear();
    TwoFactorAPI.mockImplementation(() => ({
      getStatus: jest.fn().mockResolvedValue(makeResponse({enabled: false, enrollmentRequired: false})),
      startEnrollment: jest.fn().mockResolvedValue(
        makeResponse({qrImage: "data:image/png;base64,qr", manualKey: "ABC123", recoveryCodes: ["one", "two"]})
      ),
      confirmEnrollment: jest.fn().mockResolvedValue(makeResponse({enabled: true})),
      disableTwoFactor: jest.fn().mockResolvedValue(makeResponse({}))
    }));
  });

  test("stores and clears recovery codes after hiding", async () => {
    const view = new TwoFactorEnrollmentView({});
    await view.componentDidMount();
    await view.handleStartEnrollment();

    expect(view.state.recoveryCodes).toEqual(["one", "two"]);

    view.handleHideRecoveryCodes();
    expect(view.state.recoveryCodes).toEqual([]);
  });

  test("successful confirmation enables two-factor and clears sensitive state", async () => {
    const view = new TwoFactorEnrollmentView({});
    await view.componentDidMount();

    view.setState({
      enrollmentStarted: true,
      otpInput: "654321",
      manualKey: "ABC123",
      recoveryCodes: ["one"],
    });

    await view.handleConfirmEnrollment();

    expect(view.state.twoFactorEnabled).toBe(true);
    expect(view.state.enrollmentStarted).toBe(false);
    expect(view.state.manualKey).toBe("");
    expect(view.state.recoveryCodes).toEqual([]);
  });
});
