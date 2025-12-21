import React from "react";
import renderer from "react-test-renderer";
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

jest.mock("qrcode.react", () => ({
  QRCodeCanvas: ({value}) => <div data-testid="qr-code" data-value={value}>QR</div>
}));

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
        makeResponse({
          qrImage: "data:image/png;base64,qr",
          manualKey: "ABC123",
          otpauthUrl: "otpauth://totp/User:Account?secret=ABC123&issuer=TestApp",
          recoveryCodes: ["one", "two"]
        })
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

  test("renders QR component when otpauthUrl is provided", async () => {
    const view = new TwoFactorEnrollmentView({});
    await view.componentDidMount();
    await view.handleStartEnrollment();

    const enrollmentCard = renderer.create(view.renderEnrollmentCard());
    const qrCode = enrollmentCard.root.findByProps({"data-testid": "qr-code"});

    expect(qrCode.props["data-value"]).toBe("otpauth://totp/User:Account?secret=ABC123&issuer=TestApp");
  });

  test("shows fallback message and manual key when no otpauthUrl is returned", async () => {
    TwoFactorAPI.mockImplementation(() => ({
      getStatus: jest.fn().mockResolvedValue(makeResponse({enabled: false, enrollmentRequired: false})),
      startEnrollment: jest.fn().mockResolvedValue(
        makeResponse({manualKey: "MISSING-KEY", recoveryCodes: ["three"]})
      ),
      confirmEnrollment: jest.fn().mockResolvedValue(makeResponse({enabled: true})),
      disableTwoFactor: jest.fn().mockResolvedValue(makeResponse({}))
    }));

    const view = new TwoFactorEnrollmentView({});
    await view.componentDidMount();
    await view.handleStartEnrollment();

    const enrollmentCard = renderer.create(view.renderEnrollmentCard());
    const manualKeyField = enrollmentCard.root.findByProps({label: "Manual key"});
    const tree = JSON.stringify(enrollmentCard.toJSON());

    expect(enrollmentCard.root.findAllByProps({"data-testid": "qr-code"})).toHaveLength(0);
    expect(tree).toContain("We could not generate a QR code for this enrollment.");
    expect(manualKeyField.props.value).toBe("MISSING-KEY");
  });
});
