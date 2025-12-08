import React, {
  forwardRef,
  useEffect,
  useImperativeHandle,
  useRef,
  useState
} from 'react';
import {t} from "i18next";

const TurnstileWidget = forwardRef(({siteKey, onVerify}, ref) => {
  const containerRef = useRef(null);
  const widgetIdRef = useRef(null);
  const [isScriptLoaded, setIsScriptLoaded] = useState(false);

  // Expose reset functionality to the parent class component
  useImperativeHandle(ref, () => ({
    resetWidget: () => {
      if (widgetIdRef.current && window.turnstile) {
        window.turnstile.reset(widgetIdRef.current);
      }
    }
  }));

  // Manage the script lifecycle
  useEffect(() => {
    // A unique ID for this specific script tag to identify it for removal
    const scriptId = 'turnstile-script-injection';
    let script = document.getElementById(scriptId);

    // Define the global callback name
    const callbackName = 'onTurnstileLoaded';

    // Setup the callback that Turnstile triggers when ready
    window[callbackName] = () => {
      setIsScriptLoaded(true);
    };

    // Script injection (if it doesn't exist yet)
    if (!script) {
      script = document.createElement('script');
      script.id = scriptId;
      // ?onload=... triggers the callback above
      script.src = `https://challenges.cloudflare.com/turnstile/v0/api.js?onload=${callbackName}&render=explicit`;
      script.async = true;
      script.defer = true;
      document.head.appendChild(script);
    } else if (window.turnstile) {
      // If the script was somehow already there (rare edge case in this specific architecture), just set ready
      setIsScriptLoaded(true);
    }

    // Cleanup
    return () => {
      // Remove the global callback
      delete window[callbackName];

      // Remove the Script Tag from the DOM
      const scriptToRemove = document.getElementById(scriptId);
      if (scriptToRemove) {
        document.head.removeChild(scriptToRemove);
      }

      // Remove the window.turnstile object
      delete window.turnstile;
    };
  }, []);

  // Render or destroy the widget based on script status
  useEffect(() => {
    // Only render if the script is loaded and there is a container
    if (isScriptLoaded && containerRef.current && !widgetIdRef.current && window.turnstile) {

      widgetIdRef.current = window.turnstile.render(containerRef.current, {
        sitekey: siteKey,
        callback: (token) => onVerify(token),
        'expired-callback': () => {
          onVerify(null); // Clear token in parent
        }
      });
    }

    // Cleanup the specific widget instance
    return () => {
      if (widgetIdRef.current && window.turnstile) {
        try {
          window.turnstile.remove(widgetIdRef.current);
        } catch (e) {
          // The turnstile object is already gone
          console.warn("The turnstile object is already gone, probably because the widget was destroyed: ", e.message || e)
        }
        widgetIdRef.current = null;
      }
    };
  }, [isScriptLoaded, siteKey, onVerify]);

  return (
    <div className="d-flex justify-content-center">
      {!isScriptLoaded &&
        <div style={{padding: '10px'}}>{t("Loading security check")}</div>}
      <div ref={containerRef}/>
    </div>
  );
});

export default TurnstileWidget;
